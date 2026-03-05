package com.damian.xBank.modules.banking.card.application.cqrs.result;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingCardResult(
    Long id,
    Long bankingAccountId,
    String cardHolder,
    String cardNumber,
    String cardCVV,
    String cardPIN,
    BigDecimal dailyLimit,
    int expirationYear,
    int expirationMonth,
    BankingCardType cardType,
    BankingCardStatus cardStatus,
    Instant createdAt,
    Instant updatedAt
) {
    public static BankingCardResult from(BankingCard bankingCard) {
        return new BankingCardResult(
            bankingCard.getId(),
            bankingCard.getBankingAccount().getId(),
            bankingCard.getHolderName(),
            bankingCard.getCardNumber(),
            bankingCard.getCardCvv(),
            bankingCard.getCardPin(),
            bankingCard.getDailyLimit(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getCardType(),
            bankingCard.getStatus(),
            bankingCard.getCreatedAt(),
            bankingCard.getUpdatedAt()
        );
    }
}
