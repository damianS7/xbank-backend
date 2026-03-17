package com.damian.xBank.modules.banking.transfer.incoming.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.incoming.domain.exception.IncomingTransferStatusTransitionException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferSameAccountException;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "incoming_transfers")
public class IncomingTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromAccountIban;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private BankingAccount toAccount;

    @Column(nullable = false)
    private String toAccountIban;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncomingTransferStatus status;

    @Column(name = "provider_authorization_id")
    private String providerAuthorizationId;

    @Column(nullable = false)
    private String reference;

    @OneToOne(
        mappedBy = "incomingTransfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private BankingTransaction transaction;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    protected IncomingTransfer() {
        this.status = IncomingTransferStatus.PENDING;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
        this.reference = "";
    }

    private IncomingTransfer(
        Long transferId,
        String fromAccountIban,
        BankingAccount toAccount,
        String toAccountIban,
        BigDecimal amount,
        String reference
    ) {
        this();
        this.id = transferId;
        this.fromAccountIban = fromAccountIban;
        this.toAccount = toAccount;
        this.toAccountIban = toAccountIban;
        this.amount = amount;
        this.reference = reference;

        if (this.toAccount != null) {
            this.toAccountIban = this.toAccount.getAccountNumber();
        }

        // validate transfer
        this.assertTransferPossible();

        // Generate transactions
        this.generateTransactions();
    }

    public static IncomingTransfer create(
        String fromAccountIban,
        BankingAccount toAccount,
        String toAccountIban,
        BigDecimal amount,
        String reference
    ) {
        return new IncomingTransfer(
            null,
            fromAccountIban,
            toAccount,
            toAccountIban,
            amount,
            reference
        );
    }

    public Long getId() {
        return id;
    }

    // for testing
    void setId(Long id) {
        this.id = id;
    }

    public IncomingTransferStatus getStatus() {
        return status;
    }

    public String getToAccountIban() {
        return toAccountIban;
    }

    public String getProviderAuthorizationId() {
        return providerAuthorizationId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankingAccount getToAccount() {
        return toAccount;
    }

    public String getFromAccountIban() {
        return fromAccountIban;
    }

    public String getReference() {
        return reference;
    }

    public BankingTransaction getTransaction() {
        return transaction;
    }

    public boolean isOwnedBy(Long userId) {

        // compare account owner id with given customer id
        return Objects.equals(toAccount.getOwner().getId(), userId);
    }

    private void setStatus(IncomingTransferStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new IncomingTransferStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        markAsUpdated();
    }

    private void generateTransactions() {
        // Generate transactions

        if (toAccount != null) {
            // create transfer transaction for the receiver of the funds
            this.transaction = BankingTransaction.create(
                BankingTransactionType.INCOMING_TRANSFER,
                toAccount,
                this,
                "Transfer from " + reference
            );
        }
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public void fail(String failedReason) {
        this.transaction.fail(failedReason);
        this.setStatus(IncomingTransferStatus.FAILED);
    }

    public void authorize(String providerAuthorizationId) {
        this.providerAuthorizationId = providerAuthorizationId;
        this.setStatus(IncomingTransferStatus.AUTHORIZED);
    }

    public void complete() {
        // deduct balance from sender account
        this.toAccount.deposit(amount);

        // add balance to destination (if internal)
        //        if (this.type == BankingTransferType.INTERNAL && this.getToAccount() != null) {
        //            this.getToAccount().deposit(amount);
        //        }

        // Confirm transactions
        this.transaction.complete();
        this.setStatus(IncomingTransferStatus.COMPLETED);
    }

    /**
     * Validate that current account and {@code toBankingAccount} have the same currency
     *
     * @throws OutgoingTransferCurrencyMismatchException if fromAccount and toAccount have different currencies
     */
    public void assertCurrenciesMatch() {
        // if currencies are different, throw exception
        if (!Objects.equals(toAccount.getCurrency(), toAccount.getCurrency())) {
            throw new OutgoingTransferCurrencyMismatchException(toAccount.getId());
        }

    }

    /**
     * Assert a transfer between {@link #toAccount} and {@link #toAccount} can be performed.
     *
     * @throws OutgoingTransferCurrencyMismatchException if toAccount and toAccount have different currencies
     * @throws OutgoingTransferSameAccountException      if toAccount and toAccount are the same
     */
    public void assertTransferPossible() {
        // check if the source account is active
        toAccount.assertActive();

        if (toAccount != null) {
            // check if the destiny account is active
            toAccount.assertActive();

            // check currencies are the same
            this.assertCurrenciesMatch();
        }
    }

    /**
     * Assert the ownership of the account belongs to {@link User}.
     *
     * @param userId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws OutgoingTransferNotOwnerException if the account does not belong to the customer
     */
    public IncomingTransfer assertOwnedBy(Long userId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new OutgoingTransferNotOwnerException(toAccount.getOwner().getId(), userId);
        }

        return this;
    }

    @Override
    public String toString() {
        return "IncomingTransfer{" +
               "id=" + id +
               ", fromAccountIban='" + fromAccountIban + '\'' +
               ", toAccountId=" + toAccount.getId() +
               ", toAccountIban='" + toAccountIban + '\'' +
               ", amount=" + amount +
               ", status=" + status +
               ", providerAuthorizationId='" + providerAuthorizationId + '\'' +
               ", reference='" + reference + '\'' +
               ", transactionId=" + transaction.getId() +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
