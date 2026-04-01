package com.damian.xBank.modules.banking.transfer.outgoing.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferSameAccountException;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.exception.OutgoingTransferStatusTransitionException;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Constructor JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "outgoing_transfers")
public class OutgoingTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id")
    private BankingAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private BankingAccount toAccount;

    @Column(nullable = false)
    private String toAccountIban;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutgoingTransferType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutgoingTransferStatus status;

    @Column(name = "provider_authorization_id")
    private String providerAuthorizationId;

    @Column(nullable = false)
    private String description;

    @OneToMany(
        mappedBy = "outgoingTransfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<BankingTransaction> transactions = new ArrayList<>();

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public static OutgoingTransfer create(
        BankingAccount fromAccount,
        BankingAccount toAccount,
        String toAccountIban,
        BigDecimal amount,
        String description
    ) {

        if (toAccount != null) {
            toAccountIban = toAccount.getAccountNumber();
        }

        OutgoingTransferType type = toAccount != null
            ? OutgoingTransferType.INTERNAL
            : OutgoingTransferType.EXTERNAL;

        OutgoingTransfer transfer = new OutgoingTransfer(
            null,
            fromAccount,
            toAccount,
            toAccountIban,
            amount,
            type,
            OutgoingTransferStatus.PENDING,
            null,
            description,
            new ArrayList<>(),
            Instant.now(),
            Instant.now()
        );

        // validate transfer
        transfer.assertTransferPossible();

        // Generate transactions
        transfer.generateTransactions();

        return transfer;
    }

    public static OutgoingTransfer reconstitute(
        Long id,
        BankingAccount fromAccount,
        BankingAccount toAccount,
        String toAccountIban,
        BigDecimal amount,
        OutgoingTransferType type,
        OutgoingTransferStatus status,
        String providerAuthorizationId,
        String description,
        List<BankingTransaction> transactions,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new OutgoingTransfer(
            id,
            fromAccount,
            toAccount,
            toAccountIban,
            amount,
            type,
            status,
            providerAuthorizationId,
            description,
            transactions,
            createdAt,
            updatedAt
        );
    }

    public List<BankingTransaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public BankingTransaction getFromTransaction() {
        return getTransactions().stream().filter(
            tx -> tx.isOwnedBy(fromAccount.getOwner().getId())
        ).findFirst().orElseThrow();
    }

    public BankingTransaction getToTransaction() {
        return getTransactions().stream().filter(
            tx -> tx.isOwnedBy(toAccount.getOwner().getId())
        ).findFirst().orElseThrow();
    }

    public boolean isOwnedBy(Long userId) {

        // compare account owner id with given customer id
        return Objects.equals(getFromAccount().getOwner().getId(), userId);
    }

    private void setStatus(OutgoingTransferStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new OutgoingTransferStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        markAsUpdated();
    }

    private void generateTransactions() {
        // Transacción de salida
        BankingTransaction fromTransaction = BankingTransaction.createOutgoingTransferTransaction(
            BankingTransactionType.OUTGOING_TRANSFER,
            fromAccount,
            this,
            description
        );

        this.transactions.add(fromTransaction);

        // Si el destino existe, crear transacción para el receptor
        if (toAccount != null) {
            BankingTransaction toTransaction = BankingTransaction.createOutgoingTransferTransaction(
                BankingTransactionType.INCOMING_TRANSFER,
                toAccount,
                this,
                "Transfer from " + fromAccount.getOwner().getProfile().getFullName()
            );

            this.transactions.add(toTransaction);
        }
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public void confirm() {
        this.fromAccount.reserveAmount(amount);
        this.setStatus(OutgoingTransferStatus.CONFIRMED);
    }

    public void reject(String rejectReason) {
        // reject transactions
        this.getTransactions().forEach((tx) -> tx.reject(rejectReason));
        this.setStatus(OutgoingTransferStatus.REJECTED);
    }

    public void fail(String failedReason) {
        this.fromAccount.releaseReservedAmount(amount);
        this.getTransactions().forEach((tx) -> tx.fail(failedReason));
        this.setStatus(OutgoingTransferStatus.FAILED);
    }

    public void authorize(String providerAuthorizationId) {
        this.providerAuthorizationId = providerAuthorizationId;
        this.setStatus(OutgoingTransferStatus.AUTHORIZED);
    }

    public void complete() {
        // deduct balance from sender account
        this.getFromAccount().captureReservedAmount(amount);

        // add balance to destination (if internal)
        if (this.type == OutgoingTransferType.INTERNAL && this.getToAccount() != null) {
            this.getToAccount().deposit(amount);
        }

        // Confirm transactions
        this.getTransactions().forEach(BankingTransaction::complete);
        this.setStatus(OutgoingTransferStatus.COMPLETED);
    }

    /**
     * Validate that current account and {@code toBankingAccount} have the same currency
     *
     * @throws OutgoingTransferCurrencyMismatchException if fromAccount and toAccount have different currencies
     */
    public void assertCurrenciesMatch() {
        // if currencies are different, throw exception
        if (!Objects.equals(fromAccount.getCurrency(), toAccount.getCurrency())) {
            throw new OutgoingTransferCurrencyMismatchException(toAccount.getId());
        }

    }

    /**
     * Validate that current account and {@code toBankingAccount} are not the same
     *
     * @return the current validator instance for chaining
     * @throws OutgoingTransferSameAccountException if fromAccount and toAccount are the same
     */
    public OutgoingTransfer assertDifferentAccounts() {

        // check bankingAccount and toBankingAccount are not the same
        if (Objects.equals(fromAccount.getId(), toAccount.getId())) {
            throw new OutgoingTransferSameAccountException(toAccount.getId());
        }

        return this;
    }

    /**
     * Assert a transfer between {@link #fromAccount} and {@link #toAccount} can be performed.
     *
     * @throws OutgoingTransferCurrencyMismatchException if fromAccount and toAccount have different currencies
     * @throws OutgoingTransferSameAccountException      if fromAccount and toAccount are the same
     */
    public void assertTransferPossible() {
        // check if the source account is active
        fromAccount.assertActive();

        // check fromAccount has funds
        fromAccount.assertSufficientFunds(amount);

        if (toAccount != null) {
            // check if the destiny account is active
            toAccount.assertActive();

            // check accounts are different
            this.assertDifferentAccounts();

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
    public OutgoingTransfer assertOwnedBy(Long userId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new OutgoingTransferNotOwnerException(getFromAccount().getOwner().getId(), userId);
        }

        return this;
    }

    @Override
    public String toString() {
        return "OutgoingTransfer{" +
               "id=" + id +
               ", fromAccount=" + fromAccount.getId() +
               ", toAccount=" + (toAccount != null ? toAccount.getId() : "null") +
               ", toAccountIban='" + toAccountIban + '\'' +
               ", amount=" + amount +
               ", type=" + type +
               ", status=" + status +
               ", providerAuthorizationId='" + providerAuthorizationId + '\'' +
               ", description='" + description + '\'' +
               ", transactions=" + transactions.size() +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
