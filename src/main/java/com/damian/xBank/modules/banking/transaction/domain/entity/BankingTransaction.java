package com.damian.xBank.modules.banking.transaction.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
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

    public BankingTransaction(BankingAccount bankingAccount) {
        this();
        this.bankingAccount = bankingAccount;
    }

    public BankingTransaction(BankingCard bankingCard) {
        this(bankingCard.getBankingAccount());
    }

    public BankingTransaction() {
        this.amount = BigDecimal.valueOf(0);
        this.status = BankingTransactionStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public BankingTransaction(BankingTransactionType type) {
        this();
        this.type = type;
    }

    public static BankingTransaction create() {
        return new BankingTransaction();
    }

    public boolean isOwnedBy(Long customerId) {
        return Objects.equals(getBankingAccount().getOwner().getId(), customerId);
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

    public BankingTransaction setStatus(BankingTransactionStatus status) {
        this.status = status;
        return this;
    }

    public BankingTransaction updateStatus(BankingTransactionStatus toStatus) {
        this.status.validateTransition(toStatus);
        this.status = toStatus;
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

    /**
     * Assert the ownership of the transaction.
     *
     * @param customerId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingTransactionNotOwnerException if the transaction does not belong to the customer
     */
    public BankingTransaction assertOwnedBy(Long customerId) {

        // compare account owner id with given customer id
        if (!isOwnedBy(customerId)) {
            throw new BankingTransactionNotOwnerException(getId(), customerId);
        }

        return this;
    }
}
