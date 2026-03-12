package com.damian.xBank.modules.banking.account.domain.model;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCurrencyMismatchException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "banking_accounts")
public class BankingAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingTransaction> accountTransactions; // TODO remove this?

    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingCard> bankingCards;

    @Column(length = 64)
    private String alias;

    @Column(length = 32, nullable = false)
    private String accountNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(precision = 15, scale = 2)
    private BigDecimal reservedBalance;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private BankingAccountType type;

    @Column(name = "account_currency")
    @Enumerated(EnumType.STRING)
    private BankingAccountCurrency currency;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private BankingAccountStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    private static final int MAX_CARDS_PER_ACCOUNT = 5;

    protected BankingAccount() {
        this.accountTransactions = new HashSet<>();
        this.bankingCards = new HashSet<>();
        this.balance = BigDecimal.valueOf(0);
        this.reservedBalance = BigDecimal.valueOf(0);
        this.currency = BankingAccountCurrency.EUR;
        this.type = BankingAccountType.SAVINGS;
        this.status = BankingAccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    BankingAccount(
        Long id,
        User user,
        String accountNumber,
        BankingAccountType type,
        BankingAccountCurrency currency,
        BigDecimal initialBalance,
        BankingAccountStatus initialStatus
    ) {
        this();
        this.id = id;
        this.accountNumber = accountNumber;
        this.user = user;
        this.type = type;
        this.currency = currency;
        this.balance = initialBalance;
        this.status = initialStatus;
        this.user.addBankingAccount(this);
    }

    public static BankingAccount create(
        User accountOwner,
        String accountNumber,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency
    ) {
        return new BankingAccount(
            null,
            accountOwner,
            accountNumber,
            accountType,
            accountCurrency,
            BigDecimal.valueOf(0),
            BankingAccountStatus.ACTIVE
        );
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BankingAccountType getType() {
        return type;
    }

    public BankingAccountCurrency getCurrency() {
        return currency;
    }

    public BankingAccountStatus getStatus() {
        return status;
    }

    private void setStatus(BankingAccountStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingAccountStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        markAsUpdated();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getCardLimit() {
        return MAX_CARDS_PER_ACCOUNT;
    }

    public String getAlias() {
        return alias;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void addTransaction(BankingTransaction transaction) {
        if (transaction.getBankingAccount() != this) {
            transaction.setBankingAccount(this);
        }

        this.accountTransactions.add(transaction);
    }

    public Set<BankingCard> getBankingCards() {
        return this.bankingCards;
    }

    public void addBankingCard(BankingCard bankingCard) {
        // check that the card can be added
        assertCanAddCard();

        if (bankingCard.getBankingAccount() != this) {
            bankingCard.setBankingAccount(this);
        }

        this.bankingCards.add(bankingCard);
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public void changeAlias(String alias) {
        this.alias = alias;
        markAsUpdated();
    }

    public void close() {
        setStatus(BankingAccountStatus.CLOSED);
    }

    public void suspend() {
        setStatus(BankingAccountStatus.SUSPENDED);
    }

    // returns true if the operation can be carried
    public boolean hasSufficientFunds(BigDecimal amount) {
        // if its 0 then balance is equal to the amount willing to spend
        // if its 1 then balance is greater than the amount willing to spend
        return this.getBalance().compareTo(amount) >= 0;
    }

    public boolean hasSufficientReservedFunds(BigDecimal amount) {
        // if its 0 then balance is equal to the amount willing to spend
        // if its 1 then balance is greater than the amount willing to spend
        return this.getReservedBalance().compareTo(amount) >= 0;
    }

    /**
     * Assert the account has sufficient funds.
     *
     * @param amount the amount to check
     * @return the current validator instance for chaining
     * @throws BankingAccountInsufficientFundsException if the account does not have sufficient funds
     */
    public void assertSufficientFunds(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(this.getId(), getBalance(), amount);
        }
    }

    public void assertSufficientReservedFunds(BigDecimal amount) {
        if (!this.hasSufficientReservedFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(this.getId(), getBalance(), amount);
        }
    }

    /**
     * Withdraw the given amount from the account balance.
     *
     * @param amount
     */
    public void withdraw(BigDecimal amount) {
        this.assertSufficientFunds(amount);
        this.balance = this.getBalance().subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.getBalance().add(amount);
    }

    public boolean isOwnedBy(Long customerId) {

        // compare account owner id with given customer id
        return Objects.equals(getOwner().getId(), customerId);
    }

    /**
     * Assert the ownership of the account belongs to {@link User}.
     *
     * @param userId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingAccountNotOwnerException if the account does not belong to the customer
     */
    public BankingAccount assertOwnedBy(Long userId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(userId)) {
            throw new BankingAccountNotOwnerException(getId(), userId);
        }

        return this;
    }

    public boolean isSuspended() {
        return getStatus() == BankingAccountStatus.SUSPENDED;
    }

    /**
     * Assert the account is not SUSPENDED.
     *
     * @throws BankingAccountSuspendedException if the account does not belong to the customer
     */
    public void assertNotSuspended() {

        // check if account is SUSPENDED
        if (isSuspended()) {
            throw new BankingAccountSuspendedException(getId());
        }
    }

    public boolean isClosed() {
        return getStatus() == BankingAccountStatus.CLOSED;
    }

    /**
     * Validate account is not CLOSED.
     *
     * @throws BankingAccountClosedException if the account does not belong to the customer
     */
    public void assertNotClosed() {
        // check if account is CLOSED
        if (isClosed()) {
            throw new BankingAccountClosedException(getId());
        }
    }

    /**
     * Assert account is not CLOSED or SUSPENDED.
     *
     * @throws BankingAccountSuspendedException if the account does not belong to the customer
     * @throws BankingAccountClosedException    if the account does not belong to the customer
     */
    public void assertActive() {
        this.assertNotSuspended();
        this.assertNotClosed();
    }

    public void activateBy(User actor) {
        if (actor.hasRole(UserRole.ADMIN)) {
            //            assertOwnedBy(actor.getId());
            setStatus(BankingAccountStatus.ACTIVE);
        }
    }

    public void closeBy(User actor) {
        // assert account is not Suspended or Closed already
        assertActive();

        // if not admin check ownership
        if (!actor.hasRole(UserRole.ADMIN)) {
            assertOwnedBy(actor.getId());
        }

        setStatus(BankingAccountStatus.CLOSED);
    }

    public void assertCanAddCard() {
        if (countActiveCards() >= MAX_CARDS_PER_ACCOUNT) {
            throw new BankingAccountCardsLimitException(getId());
        }
    }

    /**
     * Counts how many active (ENABLED) cards are associated with the given banking account.
     *
     * @return the number of active cards
     */
    public int countActiveCards() {
        return (int) this
            .getBankingCards()
            .stream()
            .filter(bankingCard -> bankingCard.getStatus().equals(BankingCardStatus.ACTIVE))
            .count();
    }

    public void assertCurrency(BankingAccountCurrency currency) {
        if (this.currency != currency) {
            throw new BankingAccountCurrencyMismatchException(getId());
        }
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void reserveAmount(BigDecimal amount) {
        assertSufficientFunds(amount);
        this.balance = this.balance.subtract(amount);
        this.reservedBalance = this.reservedBalance.add(amount);
    }

    public void releaseReservedAmount(BigDecimal amount) {
        assertSufficientReservedFunds(amount);
        this.reservedBalance = this.reservedBalance.subtract(amount);
        this.balance = this.balance.add(amount);
    }

    public void captureReservedAmount(BigDecimal amount) {
        assertSufficientReservedFunds(amount);
        this.reservedBalance = this.reservedBalance.subtract(amount);
    }
}
