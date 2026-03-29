package com.damian.xBank.modules.banking.account.domain.model;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountClosedException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountCurrencyMismatchException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountInsufficientReservedFundsException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountStatusTransitionException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountSuspendedException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.model.CardNumber;
import com.damian.xBank.modules.user.user.domain.model.User;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Constructor JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "banking_accounts")
public class BankingAccount {
    public static final int MAX_CARDS_PER_ACCOUNT = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    @Column(length = 32, nullable = false)
    private String accountNumber;

    @Column(length = 64)
    private String alias;

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

    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingCard> bankingCards;

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
            null,
            BigDecimal.valueOf(0),
            BigDecimal.valueOf(0),
            accountType,
            accountCurrency,
            BankingAccountStatus.ACTIVE,
            Instant.now(),
            Instant.now(),
            new HashSet<>()
        );
    }

    public static BankingAccount reconstitute(
        Long id,
        User accountOwner,
        String accountNumber,
        String alias,
        BigDecimal balance,
        BigDecimal reservedBalance,
        BankingAccountType accountType,
        BankingAccountCurrency accountCurrency,
        BankingAccountStatus status,
        Instant createdAt,
        Instant updatedAt,
        Set<BankingCard> bankingCards
    ) {
        return new BankingAccount(
            id,
            accountOwner,
            accountNumber,
            alias,
            balance,
            reservedBalance,
            accountType,
            accountCurrency,
            status,
            createdAt,
            updatedAt,
            bankingCards
        );
    }


    public User getOwner() {
        return user;
    }

    public boolean isSuspended() {
        return getStatus() == BankingAccountStatus.SUSPENDED;
    }

    public boolean isOwnedBy(Long customerId) {

        // compare account owner id with given customer id
        return Objects.equals(getOwner().getId(), customerId);
    }

    public boolean isClosed() {
        return getStatus() == BankingAccountStatus.CLOSED;
    }

    /**
     * @return El número de tarjetas activas que tiene la cuenta
     */
    public int countActiveCards() {
        return (int) this
            .getBankingCards()
            .stream()
            .filter(bankingCard -> bankingCard.getStatus().equals(BankingCardStatus.ACTIVE))
            .count();
    }

    /**
     * Comprueba si la cuenta tiene suficientes fondos
     *
     * @param amount La cantidad a comprobar
     * @return True si tiene fondos, false sino.
     */
    public boolean hasSufficientFunds(BigDecimal amount) {
        return this.getBalance().compareTo(amount) >= 0;
    }

    /**
     * Comprueba si la cuenta tiene suficientes fondos reservados.
     *
     * @param amount La cantidad a comprobar
     * @return True si tiene fondos, false sino.
     */
    public boolean hasSufficientReservedFunds(BigDecimal amount) {
        return this.getReservedBalance().compareTo(amount) >= 0;
    }

    private void setStatus(BankingAccountStatus newStatus) {
        // Si el nuevo estado es el mismo que el actual ...
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

    /**
     * Emite una tarjeta asociada a la cuenta.
     *
     * @param type
     * @param number
     * @param cvv
     * @param pin
     * @return La tarjeta emitida.
     */
    public BankingCard issueCard(
        BankingCardType type,
        String number,
        String cvv,
        String pin
    ) {
        // Comprueba que se pueda agregar una nueva tarjeta
        assertCanAddCard();

        // Crea la tarjeta
        BankingCard card = BankingCard.create(
            type,
            this,
            CardNumber.of(number),
            cvv,
            pin
        );

        this.bankingCards.add(card);
        return card;
    }

    public void changeAlias(String alias) {
        this.alias = alias;
        markAsUpdated();
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    /**
     * Retira una cantidad de la cuenta
     *
     * @param amount
     */
    public void withdraw(BigDecimal amount) {
        this.assertSufficientFunds(amount);
        this.balance = this.getBalance().subtract(amount);
        markAsUpdated();
    }

    /**
     * Deposita una cantidad en la cuenta
     *
     * @param amount
     */
    public void deposit(BigDecimal amount) {
        this.balance = this.getBalance().add(amount);
        markAsUpdated();
    }

    public void close() {
        setStatus(BankingAccountStatus.CLOSED);
    }

    public void suspend() {
        setStatus(BankingAccountStatus.SUSPENDED);
    }

    public void activate() {
        setStatus(BankingAccountStatus.ACTIVE);
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

    /**
     * Asegura que la cuenta tiene suficientes fondos o lanza excepción
     *
     * @param amount La cantidad a comprobar
     * @throws BankingAccountInsufficientFundsException Si la cuenta no tiene fondos
     */
    public void assertSufficientFunds(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingAccountInsufficientFundsException(this.getId(), getBalance(), amount);
        }
    }

    /**
     * Asegura que la cuenta tiene suficientes fondos reservados o lanza excepción
     *
     * @param amount La cantidad a comprobar
     */
    public void assertSufficientReservedFunds(BigDecimal amount) {
        if (!this.hasSufficientReservedFunds(amount)) {
            throw new BankingAccountInsufficientReservedFundsException(this.getId(), getReservedBalance(), amount);
        }
    }

    /**
     * Asegura que el owner de la cuenta sea el userId
     *
     * @param userId El userId que debe ser el owner
     * @throws BankingAccountNotOwnerException Si la cuenta no le pertenece
     */
    public void assertOwnedBy(Long userId) {
        if (!isOwnedBy(userId)) {
            throw new BankingAccountNotOwnerException(getId(), userId);
        }
    }

    /**
     * Asegura que la cuenta no esté suspendida.
     *
     * @throws BankingAccountSuspendedException Si la cuenta está suspendida
     */
    public void assertNotSuspended() {
        if (isSuspended()) {
            throw new BankingAccountSuspendedException(getId());
        }
    }

    public void assertNotClosed() {
        // check if account is CLOSED
        if (isClosed()) {
            throw new BankingAccountClosedException(getId());
        }
    }

    public void assertActive() {
        this.assertNotSuspended();
        this.assertNotClosed();
    }

    public void assertCanAddCard() {
        if (countActiveCards() >= MAX_CARDS_PER_ACCOUNT) {
            throw new BankingAccountCardsLimitException(getId());
        }
    }

    public void assertCurrency(BankingAccountCurrency currency) {
        if (this.currency != currency) {
            throw new BankingAccountCurrencyMismatchException(getId());
        }
    }

    public String toString() {
        return "BankingAccount{" +
               "id=" + id +
               ", user=" + user.getId() +
               ", accountNumber='" + accountNumber + '\'' +
               ", balance=" + balance +
               ", type=" + type +
               ", currency=" + currency +
               ", status=" + status +
               ", cards=" + (bankingCards != null ? bankingCards.size() : "null") +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
