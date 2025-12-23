package com.damian.xBank.modules.banking.account.domain.entity;

import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "banking_accounts")
public class BankingAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingTransaction> accountTransactions;

    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingCard> bankingCards;

    @Column(length = 64)
    private String alias;

    @Column(length = 32, nullable = false)
    private String accountNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private BankingAccountType accountType;

    @Enumerated(EnumType.STRING)
    private BankingAccountCurrency accountCurrency;

    @Enumerated(EnumType.STRING)
    private BankingAccountStatus accountStatus;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public BankingAccount() {
        this.accountTransactions = new HashSet<>();
        this.bankingCards = new HashSet<>();
        this.balance = BigDecimal.valueOf(0);
        this.accountCurrency = BankingAccountCurrency.EUR;
        this.accountType = BankingAccountType.SAVINGS;
        this.accountStatus = BankingAccountStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public BankingAccount(Customer customer) {
        this();
        this.customer = customer;
        this.customer.addBankingAccount(this);
    }

    public BankingAccount(
            String accountNumber,
            BankingAccountType accountType,
            BankingAccountCurrency accountCurrency
    ) {
        this();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.accountCurrency = accountCurrency;
    }

    public static BankingAccount create() {
        return new BankingAccount();
    }

    public Long getId() {
        return id;
    }

    public BankingAccount setId(Long id) {
        this.id = id;
        return this;
    }

    public Customer getOwner() {
        return customer;
    }

    public BankingAccount setOwner(Customer customer) {
        this.customer = customer;
        return this;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BankingAccount setAccountNumber(String number) {
        this.accountNumber = number;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BankingAccount setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BankingAccountType getAccountType() {
        return accountType;
    }

    public BankingAccount setAccountType(BankingAccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public BankingAccountCurrency getAccountCurrency() {
        return accountCurrency;
    }

    public BankingAccount setAccountCurrency(BankingAccountCurrency accountCurrency) {
        this.accountCurrency = accountCurrency;
        return this;
    }

    public BankingAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public BankingAccount setAccountStatus(BankingAccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BankingAccount setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Set<BankingTransaction> getAccountTransactions() {
        return accountTransactions;
    }

    public BankingAccount setAccountTransactions(Set<BankingTransaction> accountTransactions) {
        this.accountTransactions = accountTransactions;
        return this;
    }

    public BankingAccount addTransaction(BankingTransaction transaction) {
        if (transaction.getBankingAccount() != this) {
            transaction.setBankingAccount(this);
        }

        this.accountTransactions.add(transaction);

        return this;
    }

    public Set<BankingCard> getBankingCards() {
        return this.bankingCards;
    }

    public BankingAccount addBankingCard(BankingCard bankingCard) {
        if (bankingCard.getBankingAccount() != this) {
            bankingCard.setAssociatedBankingAccount(this);
        }

        this.bankingCards.add(bankingCard);

        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BankingAccount setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public BankingAccount setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    // returns true if the operation can be carried
    public boolean hasSufficientFunds(BigDecimal amount) {
        // if its 0 then balance is equal to the amount willing to spend
        // if its 1 then balance is greater than the amount willing to spend
        return this.getBalance().compareTo(amount) >= 0;
    }

    public void subtractBalance(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(this.getId());
        }

        this.setBalance(this.getBalance().subtract(amount));
    }

    public BigDecimal addBalance(BigDecimal amount) {
        this.setBalance(this.getBalance().add(amount));
        return this.getBalance();
    }
}
