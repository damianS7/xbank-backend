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

    @Column(length = 20, nullable = false)
    private String cardNumber;

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
        // for JPA
    }

    BankingCard(
        Long cardId,
        BankingCardType cardType,
        BankingCardStatus cardStatus,
        BankingAccount bankingAccount,
        String cardNumber,
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
        String cardNumber,
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
        return cardNumber;
    }

    public BankingCardType getCardType() {
        return cardType;
    }

    public BankingCardStatus getStatus() {
        return status;
    }

    private void setStatus(BankingCardStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
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

    public void changePIN(String cardPin) {
        this.cardPin = cardPin;
        markAsUpdated();
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void limit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
        markAsUpdated();
    }

    public BigDecimal getBalance() {
        return this.getBankingAccount().getBalance();
    }

    // returns true if the operation can be carried
    public boolean hasSufficientFunds(BigDecimal amount) {
        // if its 0 then balance is equal to the amount willing to spend
        // if its 1 then balance is greater than the amount willing to spend
        return this.getBankingAccount().hasSufficientFunds(amount);
    }

    /**
     * Assert the has sufficient funds.
     *
     * @param amount the amount to check
     * @return the current validator instance for chaining
     * @throws BankingCardInsufficientFundsException if the card does not have sufficient funds
     */
    public BankingCard assertSufficientFunds(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingCardInsufficientFundsException(this.getId(), getBalance(), amount);
        }

        return this;
    }

    public BankingCard chargeAmount(BigDecimal amount) {
        // assert sufficient funds or throw exception
        this.assertSufficientFunds(amount);

        this.getBankingAccount().withdraw(amount);
        return this;
    }

    public String getHolderName() {
        return this.getBankingAccount().getOwner().getProfile().getFullName();
    }

    public boolean isLocked() {
        return this.getStatus() == BankingCardStatus.LOCKED;
    }

    public boolean isDisabled() {
        return this.status == BankingCardStatus.DISABLED;
    }

    public boolean isOwnedBy(Long userId) {

        // compare card owner id with given user id
        return Objects.equals(getOwner().getId(), userId);
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    /**
     * Assert the ownership of the card belongs to {@link User}.
     *
     * @param userId the user to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingCardNotOwnerException if the card does not belong to the user
     */
    public BankingCard assertOwnedBy(Long userId) {

        // compare card owner id with given user id
        if (!isOwnedBy(userId)) {
            throw new BankingCardNotOwnerException(getId(), userId);
        }

        return this;
    }

    /**
     * Assert the card PIN matches.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardInvalidPinException if the card PIN does not equals to the given PIN
     */
    public BankingCard assertCorrectPin(String PIN) {

        // check card pin
        if (!Objects.equals(getCardPin(), PIN)) {
            throw new BankingCardInvalidPinException(getId());
        }

        return this;
    }

    /**
     * Assert the card cvv matches.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardInvalidCvvException if the card CVV does not equals to the given CVV
     */
    public BankingCard assertCorrectCvv(String cvv) {

        // check card pin
        if (!Objects.equals(getCardCvv(), cvv)) {
            throw new BankingCardInvalidCvvException(getId());
        }

        return this;
    }

    public BankingCard assertActivated() {
        // check card status
        if (status != BankingCardStatus.ACTIVE) {
            throw new BankingCardNotActiveException(getId());
        }

        return this;
    }

    public BankingCard assertNotExpired() {
        // check card expiration
        if (expiration.isExpired()) {
            throw new BankingCardExpiredException(getId());
        }

        return this;
    }

    /**
     * Assert card is not DISABLED.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardDisabledException if the card is locked
     */
    public BankingCard assertEnabled() {

        // check card status
        if (isDisabled()) {
            throw new BankingCardDisabledException(getId());
        }

        return this;
    }

    /**
     * Assert card is not LOCKED.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardLockedException if the card is locked
     */
    public BankingCard assertUnlocked() {

        // check lock status
        if (isLocked()) {
            throw new BankingCardLockedException(getId());
        }

        return this;
    }

    /**
     * Assert card is not DISABLED or LOCKED and can be used for any operation.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardDisabledException if the card is disabled
     * @throws BankingCardLockedException   if the card is locked
     */
    public BankingCard assertUsable() {

        this.assertActivated()
            .assertNotExpired()
            .assertUnlocked();

        return this;
    }

    /**
     * Validate that current card can spend.
     *
     * @param amount  the amount to spend
     * @param cardPin the pin of the card
     * @return the current validator instance for chaining
     */
    public BankingCard assertCanSpend(
        User actor,
        BigDecimal amount,
        String cardPin
    ) {
        // check the account status and see if can be used to operate
        // run validations for the card and throw exception
        this.assertOwnedBy(actor.getId())
            .assertUsable()
            .assertCorrectPin(cardPin)
            .assertSufficientFunds(amount);

        return this;
    }

    /**
     * Validate input year equals to the card expiration year
     *
     * @param year
     */
    public void validateExpirationYear(int year) {
        if (this.getExpiration().getYear() != year) {
            throw new BankingCardInvalidExpirationYearException(this.id);
        }
    }

    /**
     * Validate input year equals to the card expiration month
     *
     * @param month
     */
    public void validateExpirationMonth(int month) {
        if (this.getExpiration().getMonth() != month) {
            throw new BankingCardInvalidExpirationMonthException(this.id);
        }
    }

    /**
     * Authorize a payment.
     *
     * @param amount
     * @param expiryMonth
     * @param expiryYear
     * @param cvv
     */
    public void authorizePayment(
        BigDecimal amount,
        Integer expiryMonth,
        Integer expiryYear,
        String cvv
    ) {
        assertUsable();
        assertSufficientFunds(amount);
        validateExpirationYear(expiryYear);
        validateExpirationMonth(expiryMonth);
        assertCorrectCvv(cvv);
    }

    /**
     * Activate the card.
     *
     * @param cvv
     */
    public void activate(String cvv) {
        assertCorrectCvv(cvv);
        setStatus(BankingCardStatus.ACTIVE);
    }

    public void deactivate(String cvv) {
    }

    public void disable() {
        setStatus(BankingCardStatus.DISABLED);
    }

    public void enable() {
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
}
