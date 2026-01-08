package com.damian.xBank.modules.banking.transaction.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusTransitionException;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "banking_transactions")
public class BankingTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private BankingAccount bankingAccount;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private BankingCard bankingCard;

    @ManyToOne
    @JoinColumn(name = "transfer_id", referencedColumnName = "id")
    private BankingTransfer transfer;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column
    private String description;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private BankingTransactionType type;

    @Enumerated(EnumType.STRING)
    private BankingTransactionStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public BankingTransaction() {
        this.amount = BigDecimal.valueOf(0);
        this.status = BankingTransactionStatus.PENDING;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    public BankingTransaction(BankingAccount bankingAccount) {
        this();
        this.bankingAccount = bankingAccount;
    }

    public BankingTransaction(BankingCard bankingCard) {
        this(bankingCard.getBankingAccount());
    }

    public static BankingTransaction create(
            BankingTransactionType type,
            BankingAccount account,
            BigDecimal amount
    ) {
        BankingTransaction transaction = new BankingTransaction();
        transaction.setBankingAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(account.getBalance());
        transaction.setBalanceAfter(transaction.calcBalanceAfter());
        return transaction;
    }

    public static BankingTransaction create(
            BankingTransactionType type,
            BankingCard card,
            BigDecimal amount
    ) {
        return create(type, card.getBankingAccount(), amount);
    }

    public boolean isOwnedBy(Long userId) {
        return Objects.equals(getBankingAccount().getOwner().getId(), userId);
    }

    public Long getId() {
        return id;
    }

    public BankingTransaction setId(Long id) {
        this.id = id;
        return this;
    }

    public BankingAccount getBankingAccount() {
        return bankingAccount;
    }

    public BankingTransaction setBankingAccount(BankingAccount account) {
        this.bankingAccount = account;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankingTransaction setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BankingTransactionType getType() {
        return type;
    }

    public BankingTransaction setType(BankingTransactionType transactionType) {
        this.type = transactionType;
        return this;
    }

    public BankingTransactionStatus getStatus() {
        return status;
    }

    public BankingTransaction setStatus(BankingTransactionStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return this;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingTransactionStatusTransitionException(
                    this.id,
                    this.status.name(),
                    newStatus.name()
            );
        }

        this.status = newStatus;
        return this;
    }

    public BankingTransfer getTransfer() {
        return transfer;
    }

    public BankingTransaction setTransfer(BankingTransfer transfer) {
        this.transfer = transfer;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BankingTransaction setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BankingTransaction setDescription(String description) {
        this.description = description;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BankingTransaction setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public BankingCard getBankingCard() {
        return bankingCard;
    }

    public BankingTransaction setBankingCard(BankingCard bankingCard) {
        this.bankingCard = bankingCard;
        return this;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public BankingTransaction setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
        return this;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public BankingTransaction setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
        return this;
    }

    private BigDecimal calcBalanceAfter() {
        if (type == BankingTransactionType.DEPOSIT || type == BankingTransactionType.TRANSFER_FROM) {
            return balanceBefore.add(this.amount);
        }

        if (type == BankingTransactionType.TRANSFER_TO || type == BankingTransactionType.CARD_CHARGE) {
            return balanceBefore.subtract(this.amount);
        }

        return balanceBefore.subtract(this.amount);
    }

    public void complete() {
        this.balanceBefore = bankingAccount.getBalance();
        this.balanceAfter = calcBalanceAfter();
        this.setStatus(BankingTransactionStatus.COMPLETED);
        this.updatedAt = Instant.now();
    }

    public void reject() {
        this.setStatus(BankingTransactionStatus.REJECTED);
        this.updatedAt = Instant.now();
    }

    /**
     * Assert the ownership of the transaction.
     *
     * @param userId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingTransactionNotOwnerException if the transaction does not belong to the customer
     */
    public BankingTransaction assertOwnedBy(Long userId) {

        // compare account owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new BankingTransactionNotOwnerException(getId(), userId);
        }

        return this;
    }
}
