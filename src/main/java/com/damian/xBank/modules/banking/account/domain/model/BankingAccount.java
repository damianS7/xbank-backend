package com.damian.xBank.modules.banking.account.domain.model;

import com.damian.xBank.modules.banking.account.domain.exception.*;
import com.damian.xBank.modules.banking.card.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.account.account.domain.model.UserAccountRole;
import com.damian.xBank.modules.user.profile.domain.entity.UserProfile;
import jakarta.persistence.*;

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
    private Set<BankingTransaction> accountTransactions;

    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingCard> bankingCards;

    @Column(length = 64)
    private String alias;

    @Column(length = 32, nullable = false)
    private String accountNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

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

    private final int MAX_CARDS_PER_ACCOUNT = 5;

    public BankingAccount() {
        this.accountTransactions = new HashSet<>();
        this.bankingCards = new HashSet<>();
        this.balance = BigDecimal.valueOf(0);
        this.currency = BankingAccountCurrency.EUR;
        this.type = BankingAccountType.SAVINGS;
        this.status = BankingAccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
        this.createdAt = Instant.now();
    }

    public BankingAccount(User user) {
        this();
        this.user = user;
        this.user.addBankingAccount(this);
    }

    public static BankingAccount create(User user) {
        return new BankingAccount(user);
    }

    public Long getId() {
        return id;
    }

    public BankingAccount setId(Long id) {
        this.id = id;
        return this;
    }

    public User getOwner() {
        return user;
    }

    public BankingAccount setOwner(User user) {
        this.user = user;
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

    public BankingAccountType getType() {
        return type;
    }

    public BankingAccount setType(BankingAccountType type) {
        this.type = type;
        return this;
    }

    public BankingAccountCurrency getCurrency() {
        return currency;
    }

    public BankingAccount setCurrency(BankingAccountCurrency accountCurrency) {
        this.currency = accountCurrency;
        return this;
    }

    public BankingAccountStatus getStatus() {
        return status;
    }

    public BankingAccount setStatus(BankingAccountStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return this;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingAccountStatusTransitionException(
                    this.id,
                    this.status.name(),
                    newStatus.name()
            );
        }

        this.updatedAt = Instant.now();
        this.status = newStatus;
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

    public int getCardLimit() {
        return MAX_CARDS_PER_ACCOUNT;
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
        // check that the card can be added
        assertCanAddCard();

        if (bankingCard.getBankingAccount() != this) {
            bankingCard.setBankingAccount(this);
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

    /**
     * Assert the account has sufficient funds.
     *
     * @param amount the amount to check
     * @return the current validator instance for chaining
     * @throws BankingAccountInsufficientFundsException if the account does not have sufficient funds
     */
    public BankingAccount assertSufficientFunds(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(this.getId(), getBalance(), amount);
        }

        return this;
    }

    public void subtractBalance(BigDecimal amount) {
        this.assertSufficientFunds(amount);

        this.setBalance(this.getBalance().subtract(amount));
    }

    public BigDecimal addBalance(BigDecimal amount) {
        this.setBalance(this.getBalance().add(amount));
        return this.getBalance();
    }

    public boolean isOwnedBy(Long customerId) {

        // compare account owner id with given customer id
        return Objects.equals(getOwner().getId(), customerId);
    }

    /**
     * Assert the ownership of the account belongs to {@link UserProfile}.
     *
     * @param customerId the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingAccountNotOwnerException if the account does not belong to the customer
     */
    public BankingAccount assertOwnedBy(Long customerId) {

        // compare card owner id with given customer id
        if (!isOwnedBy(customerId)) {
            throw new BankingAccountNotOwnerException(getId(), customerId);
        }

        return this;
    }

    public boolean isSuspended() {
        return getStatus() == BankingAccountStatus.SUSPENDED;
    }

    /**
     * Assert the account is not SUSPENDED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountSuspendedException if the account does not belong to the customer
     */
    public BankingAccount assertNotSuspended() {

        // check if account is SUSPENDED
        if (isSuspended()) {
            throw new BankingAccountSuspendedException(getId());
        }

        return this;
    }

    public boolean isClosed() {
        return getStatus() == BankingAccountStatus.CLOSED;
    }

    /**
     * Validate account is not CLOSED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountClosedException if the account does not belong to the customer
     */
    public BankingAccount assertNotClosed() {
        // check if account is CLOSED
        if (isClosed()) {
            throw new BankingAccountClosedException(getId());
        }

        return this;
    }

    /**
     * Assert account is not CLOSED or SUSPENDED.
     *
     * @return the current validator instance for chaining
     * @throws BankingAccountSuspendedException if the account does not belong to the customer
     * @throws BankingAccountClosedException    if the account does not belong to the customer
     */
    public BankingAccount assertActive() {
        this.assertNotSuspended();
        this.assertNotClosed();
        return this;
    }

    public void activateBy(User actor) {
        if (actor.hasRole(UserAccountRole.ADMIN)) {
            //            assertOwnedBy(actor.getId());
            setStatus(BankingAccountStatus.ACTIVE);
        }

    }

    public void closeBy(User actor) {
        // assert account is not Suspended or Closed already
        assertActive();

        // if not admin check ownership
        if (!actor.hasRole(UserAccountRole.ADMIN)) {
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
}
