package com.damian.xBank.modules.banking.card.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;

import java.math.BigDecimal;

public class BankingCardTestBuilder {
    private Long id = null;
    private BankingAccount owner;
    private BankingCardType cardType = BankingCardType.CREDIT;
    private CardExpiration expiration = CardExpiration.defaultExpiration();
    private BigDecimal dailyLimit = BigDecimal.valueOf(1000);
    private CardNumber cardNumber = new CardNumber("1234567890123456");
    private String cvv = "123";
    private String pin = "1234";
    private BankingCardStatus status = BankingCardStatus.ACTIVE;

    public static BankingCardTestBuilder builder() {
        return new BankingCardTestBuilder();
    }

    public BankingCardTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BankingCardTestBuilder withOwnerAccount(BankingAccount owner) {
        this.owner = owner;
        return this;
    }

    public BankingCardTestBuilder withType(BankingCardType type) {
        this.cardType = type;
        return this;
    }

    public BankingCardTestBuilder withCardNumber(String cardNumber) {
        this.cardNumber = new CardNumber(cardNumber);
        return this;
    }

    public BankingCardTestBuilder withPIN(String pin) {
        this.pin = pin;
        return this;
    }

    public BankingCardTestBuilder withCVV(String cvv) {
        this.cvv = cvv;
        return this;
    }

    public BankingCardTestBuilder withStatus(BankingCardStatus status) {
        this.status = status;
        return this;
    }

    public BankingCard build() {
        return new BankingCard(
            id, cardType, status, owner, cardNumber, expiration, cvv, pin, dailyLimit
        );
    }
}