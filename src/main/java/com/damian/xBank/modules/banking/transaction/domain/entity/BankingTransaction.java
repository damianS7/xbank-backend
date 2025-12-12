package com.damian.xBank.modules.banking.transaction.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

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

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    private BankingTransactionType transactionType;

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

    public BankingTransaction(BankingTransactionType transactionType) {
        this();
        this.transactionType = transactionType;
    }

    public static BankingTransaction create() {
        return new BankingTransaction();
    }

    public boolean belongsTo(Customer customer) {
        return getBankingAccount()
                .getOwner()
                .getId()
                .equals(customer.getId());
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
        return transactionType;
    }

    public BankingTransaction setType(BankingTransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }


    public BankingTransactionStatus getStatus() {
        return status;
    }

    public BankingTransaction setStatus(BankingTransactionStatus status) {
        this.status = status;
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
}
