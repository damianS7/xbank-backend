package com.damian.xBank.modules.banking.card.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.exception.*;
import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

    @Column
    private LocalDate expiredDate;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "card_status", columnDefinition = "banking_card_status_type")
    private BankingCardStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public BankingCard() {
        this.status = BankingCardStatus.PENDING_ACTIVATION;
        this.cardType = BankingCardType.DEBIT;
        this.dailyLimit = BigDecimal.valueOf(3000);
    }

    public BankingCard(BankingAccount bankingAccount) {
        this();
        this.bankingAccount = bankingAccount;
    }

    public static BankingCard create(BankingAccount bankingAccount) {
        return new BankingCard(bankingAccount);
    }

    public Long getId() {
        return id;
    }

    public BankingCard setId(Long id) {
        this.id = id;
        return this;
    }

    public User getOwner() {
        return bankingAccount.getOwner();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public BankingCard setCardNumber(String number) {
        this.cardNumber = number;
        return this;
    }

    public BankingCardType getCardType() {
        return cardType;
    }

    public BankingCard setCardType(BankingCardType cardType) {
        this.cardType = cardType;
        return this;
    }

    public BankingCardStatus getStatus() {
        return status;
    }

    public BankingCard setStatus(BankingCardStatus newStatus) {
        // if the actual status is the same as the new ... do nothing
        if (this.status == newStatus) {
            return this;
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new BankingCardStatusTransitionException(
                    this.id,
                    this.status.name(),
                    newStatus.name()
            );
        }

        this.updatedAt = Instant.now();
        this.status = newStatus;
        return this;
    }

    public BankingAccount getBankingAccount() {
        return bankingAccount;
    }

    public BankingCard setBankingAccount(BankingAccount bankingAccount) {
        this.bankingAccount = bankingAccount;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BankingCard setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public BankingCard setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public BankingCard setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
        return this;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public BankingCard setCardCvv(String CVV) {
        this.cardCvv = CVV;
        return this;
    }

    public String getCardPin() {
        return cardPin;
    }

    public BankingCard setCardPin(String cardPin) {
        this.cardPin = cardPin;
        return this;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BankingCard setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
        return this;
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

        this.getBankingAccount().subtractBalance(amount);
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
        if (this.getExpiredDate().getYear() != year) {
            throw new BankingCardInvalidExpirationYearException(this.id);
        }
    }

    /**
     * Validate input year equals to the card expiration month
     *
     * @param month
     */
    public void validateExpirationMonth(int month) {
        if (this.getExpiredDate().getMonth().getValue() != month) {
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
}
