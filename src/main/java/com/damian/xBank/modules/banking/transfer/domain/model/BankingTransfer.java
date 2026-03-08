package com.damian.xBank.modules.banking.transfer.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferCurrencyMismatchException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferNotOwnerException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferSameAccountException;
import com.damian.xBank.modules.banking.transfer.domain.exception.BankingTransferStatusTransitionException;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "banking_transfers")
public class BankingTransfer {
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
    private BankingTransferType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankingTransferStatus status;

    @Column(name = "provider_authorization_id")
    private String providerAuthorizationId;

    @Column(nullable = false)
    private String description;

    @OneToMany(
        mappedBy = "transfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<BankingTransaction> transactions = new ArrayList<>();

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public BankingTransfer() {
        this.type = BankingTransferType.INTERNAL;
        this.status = BankingTransferStatus.PENDING;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
        this.description = "";
    }

    public static BankingTransfer create(
        BankingAccount fromAccount,
        BankingAccount toAccount,
        BigDecimal amount
    ) {
        BankingTransferType type = toAccount != null
            ? BankingTransferType.INTERNAL
            : BankingTransferType.EXTERNAL;

        // Create the transfer
        BankingTransfer transfer = new BankingTransfer();
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setAmount(amount);
        transfer.setType(type);

        // validate transfer
        transfer.assertTransferPossible();

        // Generate transactions
        transfer.generateTransactions();

        return transfer;
    }

    private void generateTransactions() {
        // Generate transactions
        BankingTransaction fromTransaction = BankingTransaction
            .create(
                BankingTransactionType.TRANSFER_TO,
                fromAccount,
                amount
            )
            .setStatus(BankingTransactionStatus.PENDING)
            .setDescription(description);

        addTransaction(fromTransaction);

        if (toAccount != null) {
            // create transfer transaction for the receiver of the funds
            BankingTransaction toTransaction = BankingTransaction
                .create(
                    BankingTransactionType.TRANSFER_FROM,
                    toAccount,
                    amount
                )
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription("Transfer from " + fromAccount.getOwner().getProfile().getFullName());

            addTransaction(toTransaction);
        }
    }

    public Long getId() {
        return id;
    }

    public BankingTransfer setId(Long id) {
        this.id = id;
        return this;
    }

    public BankingTransferStatus getStatus() {
        return status;
    }

    public BankingTransfer setStatus(BankingTransferStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return this;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingTransferStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BankingTransfer setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BankingTransfer setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankingTransfer setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BankingAccount getToAccount() {
        return toAccount;
    }

    public BankingTransfer setToAccount(BankingAccount toAccount) {
        this.toAccount = toAccount;
        if (this.toAccount != null) {
            this.toAccountIban = this.toAccount.getAccountNumber();
        }
        return this;
    }

    public BankingAccount getFromAccount() {
        return fromAccount;
    }

    public String getDescription() {
        return description;
    }

    public BankingTransfer setDescription(String description) {
        this.description = description;
        return this;
    }

    public BankingTransfer setFromAccount(BankingAccount fromAccount) {
        this.fromAccount = fromAccount;
        return this;
    }

    public void addTransaction(BankingTransaction tx) {
        tx.setTransfer(this);
        this.transactions.add(tx);
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

    /**
     * Assert the ownership of the account belongs to {@link User}.
     *
     * @param userId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingTransferNotOwnerException if the account does not belong to the customer
     */
    public BankingTransfer assertOwnedBy(Long userId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new BankingTransferNotOwnerException(getFromAccount().getOwner().getId(), userId);
        }

        return this;
    }

    public void confirm() {
        // TODO assertIsPending();
        this.setStatus(BankingTransferStatus.CONFIRMED);
        this.updatedAt = Instant.now();
    }

    public void authorize() {
        // deduct balance from source
        this.getFromAccount().subtractBalance(getAmount());

        // add balance to source (if internal)
        if (this.getToAccount() != null) {
            this.getToAccount().addBalance(getAmount());
        }

        // Confirm transactions
        this.getTransactions().forEach(BankingTransaction::complete);
        this.setStatus(BankingTransferStatus.AUTHORIZED);
        this.updatedAt = Instant.now();
    }

    public void reject(String rejectReason) {
        // reject transactions
        this.getTransactions().forEach((tx) -> tx.fail(rejectReason));
        this.setStatus(BankingTransferStatus.REJECTED);
        this.updatedAt = Instant.now();
    }

    /**
     * Validate that current account and {@code toBankingAccount} have the same currency
     *
     * @return the current validator instance for chaining
     * @throws BankingTransferCurrencyMismatchException if fromAccount and toAccount have different currencies
     */
    public BankingTransfer assertCurrenciesMatch() {

        // if currencies are different, throw exception
        if (!Objects.equals(fromAccount.getCurrency(), toAccount.getCurrency())) {
            throw new BankingTransferCurrencyMismatchException(toAccount.getId());
        }

        return this;
    }

    /**
     * Validate that current account and {@code toBankingAccount} are not the same
     *
     * @return the current validator instance for chaining
     * @throws BankingTransferSameAccountException if fromAccount and toAccount are the same
     */
    public BankingTransfer assertDifferentAccounts() {

        // check bankingAccount and toBankingAccount are not the same
        if (Objects.equals(fromAccount.getId(), toAccount.getId())) {
            throw new BankingTransferSameAccountException(toAccount.getId());
        }

        return this;
    }

    /**
     * Assert a transfer between {@link #fromAccount} and {@link #toAccount} can be performed.
     *
     * @return the current BankingTransfer
     * @throws BankingTransferCurrencyMismatchException if fromAccount and toAccount have different currencies
     * @throws BankingTransferSameAccountException      if fromAccount and toAccount are the same
     */
    public BankingTransfer assertTransferPossible() {
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

        return this;
    }

    public BankingTransferType getType() {
        return type;
    }

    public BankingTransfer setType(BankingTransferType type) {
        this.type = type;
        return this;
    }

    public String getToAccountIban() {
        return toAccountIban;
    }

    public BankingTransfer setToAccountIban(String toAccountIban) {
        this.toAccountIban = toAccountIban;
        return this;
    }

    public String getProviderAuthorizationId() {
        return providerAuthorizationId;
    }

    public BankingTransfer setProviderAuthorizationId(String providerAuthorizationId) {
        this.providerAuthorizationId = providerAuthorizationId;
        return this;
    }
}
