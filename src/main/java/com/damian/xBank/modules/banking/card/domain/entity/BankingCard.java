package com.damian.xBank.modules.banking.card.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardLockStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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
    private BankingCardStatus cardStatus;

    @Enumerated(EnumType.STRING)
    private BankingCardLockStatus lockStatus;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public BankingCard() {
        this.cardStatus = BankingCardStatus.ACTIVE;
        this.cardType = BankingCardType.DEBIT;
        this.lockStatus = BankingCardLockStatus.UNLOCKED;
        this.dailyLimit = BigDecimal.valueOf(3000);
    }

    public BankingCard(BankingAccount bankingAccount) {
        this();
        this.bankingAccount = bankingAccount;
    }

    public BankingCard(BankingAccount bankingAccount, String cardNumber, BankingCardType cardType) {
        this();
        this.bankingAccount = bankingAccount;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }

    public static BankingCard create() {
        return new BankingCard();
    }

    public Long getId() {
        return id;
    }

    public BankingCard setId(Long id) {
        this.id = id;
        return this;
    }

    public Customer getOwner() {
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

    public BankingCardStatus getCardStatus() {
        return cardStatus;
    }

    public BankingCard setCardStatus(BankingCardStatus cardStatus) {
        this.cardStatus = cardStatus;
        return this;
    }

    public BankingAccount getBankingAccount() {
        return bankingAccount;
    }

    public BankingCard setAssociatedBankingAccount(BankingAccount bankingAccount) {
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

    public BankingCardLockStatus getLockStatus() {
        return lockStatus;
    }

    public BankingCard setLockStatus(BankingCardLockStatus lockStatus) {
        this.lockStatus = lockStatus;
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

    public BankingCard chargeAmount(BigDecimal amount) {
        if (!this.hasSufficientFunds(amount)) {
            throw new BankingCardInsufficientFundsException(this.getId());
        }

        this.getBankingAccount().subtractBalance(amount);
        return this;
    }

    public String getHolderName() {
        return this.getBankingAccount().getOwner().getFullName();
    }

    public boolean isLocked() {
        return this.lockStatus == BankingCardLockStatus.LOCKED;
    }

    public boolean isDisabled() {
        return this.cardStatus == BankingCardStatus.DISABLED;
    }

    public boolean isUsable() {
        return !isDisabled() && !isLocked();
    }
}
