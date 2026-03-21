package com.damian.xBank.modules.banking.card.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardDisabledException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardExpiredException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidCvvException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidExpirationMonthException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidExpirationYearException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInvalidPinException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardLockedException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotActiveException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotOwnerException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardStatusTransitionException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "banking_cards")
public class BankingCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private BankingAccount bankingAccount;

    @Embedded
    private CardNumber cardNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal dailyLimit;

    @Enumerated(EnumType.STRING)
    private BankingCardType cardType;

    @Column(length = 3)
    private String cardCvv;

    @Column(length = 4)
    private String cardPin;

    @Embedded
    private CardExpiration expiration;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "card_status", columnDefinition = "banking_card_status_type")
    private BankingCardStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    protected BankingCard() {
        // constructor JPA
    }

    BankingCard(
        Long cardId,
        BankingCardType cardType,
        BankingCardStatus cardStatus,
        BankingAccount bankingAccount,
        CardNumber cardNumber,
        CardExpiration expiration,
        String cardCvv,
        String cardPin,
        BigDecimal dailyLimit
    ) {
        this.id = cardId;
        this.cardType = cardType;
        this.status = cardStatus;
        this.bankingAccount = bankingAccount;
        this.cardNumber = cardNumber;
        this.expiration = expiration;
        this.cardCvv = cardCvv;
        this.cardPin = cardPin;
        this.dailyLimit = dailyLimit;
        this.createdAt = Instant.now();
    }

    public static BankingCard create(
        BankingCardType bankingCardType,
        BankingAccount bankingAccount,
        CardNumber cardNumber,
        String cardCvv,
        String cardPin
    ) {
        return new BankingCard(
            null,
            bankingCardType,
            BankingCardStatus.PENDING_ACTIVATION,
            bankingAccount,
            cardNumber,
            CardExpiration.defaultExpiration(),
            cardCvv,
            cardPin,
            BigDecimal.valueOf(3000)
        );
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return bankingAccount.getOwner();
    }

    public String getCardNumber() {
        return cardNumber.getValue();
    }

    public BankingCardType getCardType() {
        return cardType;
    }

    public BankingCardStatus getStatus() {
        return status;
    }

    public BankingAccount getBankingAccount() {
        return bankingAccount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public CardExpiration getExpiration() {
        return expiration;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public String getCardPin() {
        return cardPin;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BigDecimal getBalance() {
        return this.getBankingAccount().getBalance();
    }

    public String getHolderName() {
        return this.getBankingAccount().getOwner().getProfile().getFullName();
    }

    private void setStatus(BankingCardStatus newStatus) {
        // Si el estado actual de la tarjeta es el mismo que el nuevo ... no hacer nada
        if (this.status == newStatus) {
            return;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingCardStatusTransitionException(
                this.id,
                this.status.name(),
                newStatus.name()
            );
        }

        this.status = newStatus;
        markAsUpdated();
    }

    /**
     * @param amount Cantidad a comparar con el balance
     * @return true si tiene fondos
     */
    public boolean hasSufficientFunds(BigDecimal amount) {
        return this.getBankingAccount().hasSufficientFunds(amount);
    }

    public boolean isLocked() {
        return this.getStatus() == BankingCardStatus.LOCKED;
    }

    public boolean isDisabled() {
        return this.status == BankingCardStatus.DISABLED;
    }

    public boolean isOwnedBy(Long userId) {
        return Objects.equals(getOwner().getId(), userId);
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public void changePIN(String cardPin) {
        this.cardPin = cardPin;
        markAsUpdated();
    }

    public void limit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
        markAsUpdated();
    }

    /**
     * Comprueba que un pago puede llevarse a cabo y lo autoriza.
     *
     * @param amount      Cantidad a comparar con el balance
     * @param expiryMonth Mes de expiración de la tarjeta
     * @param expiryYear  Año de expiración de la tarjeta
     * @param cvv         CVV de la tarjeta
     */
    public void authorize(BigDecimal amount, Integer expiryMonth, Integer expiryYear, String cvv) {
        assertUsable();
        assertSufficientFunds(amount);
        validateExpirationYear(expiryYear);
        validateExpirationMonth(expiryMonth);
        assertCorrectCvv(cvv);
    }

    public void capture(BankingTransaction transaction) {
        this.charge(transaction.getAmount());
        transaction.capture();
    }

    public void charge(BigDecimal amount) {
        this.assertSufficientFunds(amount);
        this.getBankingAccount().withdraw(amount);
        markAsUpdated();
    }

    public void withdraw(BigDecimal amount) {
        this.assertSufficientFunds(amount);
        this.getBankingAccount().withdraw(amount);
        markAsUpdated();
    }

    /**
     * Activa la tarjeta
     *
     * @param cvv Código CVV necesario para la activación
     */
    public void activate(String cvv) {
        assertCorrectCvv(cvv);
        setStatus(BankingCardStatus.ACTIVE);
    }

    public void disable() {
        setStatus(BankingCardStatus.DISABLED);
    }

    public void lock() {
        setStatus(BankingCardStatus.LOCKED);
    }

    public void unlock() {
        setStatus(BankingCardStatus.ACTIVE);
    }

    public void expired() {
        setStatus(BankingCardStatus.EXPIRED);
    }

    /**
     * Asegura que se tienen fondos suficientes.
     *
     * @param amount Cantidad a comparar con el balance
     * @throws BankingCardInsufficientFundsException Si la tarjeta no tiene fondos
     */
    public void assertSufficientFunds(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingCardInsufficientFundsException(this.getId(), getBalance(), amount);
        }
    }

    /**
     * Asegura el ownership de la tarjeta con {@link User}.
     *
     * @param userId El ID de usuario a comparar con el dueño de la tarjeta
     * @throws BankingCardNotOwnerException Si la tarjeta no pertenece al user ID
     */
    public void assertOwnedBy(Long userId) {

        // compare card owner id with given user id
        if (!isOwnedBy(userId)) {
            throw new BankingCardNotOwnerException(getId(), userId);
        }
    }

    /**
     * Assert que el PIN de la tarjeta coincide.
     *
     * @throws BankingCardInvalidPinException Si {@code PIN} no coincide con el de la tarjeta
     */
    public void assertCorrectPin(String PIN) {
        if (!Objects.equals(getCardPin(), PIN)) {
            throw new BankingCardInvalidPinException(getId());
        }
    }

    /**
     * Assert que el CVV coincide con el de la tarjeta.
     *
     * @throws BankingCardInvalidCvvException Si {@code cvv} no coincide con el cvv de la tarjeta
     */
    public void assertCorrectCvv(String cvv) {
        if (!Objects.equals(getCardCvv(), cvv)) {
            throw new BankingCardInvalidCvvException(getId());
        }
    }

    public void assertActivated() {
        if (status != BankingCardStatus.ACTIVE) {
            throw new BankingCardNotActiveException(getId());
        }
    }

    public void assertNotExpired() {
        if (expiration.isExpired()) {
            throw new BankingCardExpiredException(getId());
        }
    }

    /**
     * Assert que la tarjeta está en estado ACTIVE.
     *
     * @throws BankingCardDisabledException si la tarjeta está en estado DISABLED
     */
    public void assertEnabled() {
        if (isDisabled()) {
            throw new BankingCardDisabledException(getId());
        }
    }

    /**
     * Assert que la tarjeta no está en estado LOCKED
     *
     * @throws BankingCardLockedException Si la tarjeta está en estado LOCKED
     */
    public void assertUnlocked() {
        if (isLocked()) {
            throw new BankingCardLockedException(getId());
        }
    }

    /**
     * Assert que la tarjeta no está DISABLED o LOCKED.
     *
     * @throws BankingCardDisabledException Si la tarjeta está DISABLED
     * @throws BankingCardLockedException   Si la tarjeta está LOCKED
     */
    public void assertUsable() {
        this.assertActivated();
        this.assertNotExpired();
        this.assertUnlocked();
    }

    /**
     * Asser que la tarjeta puede gastar la cantidad indicada.
     *
     * @param amount  La cantidad a gastar
     * @param cardPin El pin de la tarjeta introducido por el usuario
     */
    public void assertCanSpend(
        User actor,
        BigDecimal amount,
        String cardPin
    ) {
        this.assertOwnedBy(actor.getId());
        this.assertUsable();
        this.assertCorrectPin(cardPin);
        this.assertSufficientFunds(amount);
    }

    /**
     * Válida el año expiración de la tarjeta
     *
     * @param year Año introducido por el usuario
     */
    public void validateExpirationYear(int year) {
        if (this.getExpiration().getYear() != year) {
            throw new BankingCardInvalidExpirationYearException(this.id);
        }
    }

    /**
     * Válida el mes expiración de la tarjeta
     *
     * @param month Mes introducido por el usuario
     */
    public void validateExpirationMonth(int month) {
        if (this.getExpiration().getMonth() != month) {
            throw new BankingCardInvalidExpirationMonthException(this.id);
        }
    }

}
