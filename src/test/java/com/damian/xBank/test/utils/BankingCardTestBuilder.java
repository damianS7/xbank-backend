package com.damian.xBank.test.utils;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.model.CardExpiration;
import com.damian.xBank.modules.banking.card.domain.model.CardNumber;

import java.math.BigDecimal;
import java.time.Instant;

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

    public BankingCardTestBuilder pendingActivation() {
        this.status = BankingCardStatus.PENDING_ACTIVATION;
        return this;
    }

    public BankingCardTestBuilder active() {
        this.status = BankingCardStatus.ACTIVE;
        return this;
    }

    public BankingCardTestBuilder locked() {
        this.status = BankingCardStatus.LOCKED;
        return this;
    }

    public BankingCardTestBuilder expired() {
        this.status = BankingCardStatus.EXPIRED;
        return this;
    }

    public BankingCardTestBuilder disabled() {
        this.status = BankingCardStatus.DISABLED;
        return this;
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
        return BankingCard.reconstitute(
            id,
            cardType,
            owner,
            cardNumber,
            dailyLimit,
            cvv,
            pin,
            expiration,
            status,
            Instant.now(),
            Instant.now()
        );
    }
}