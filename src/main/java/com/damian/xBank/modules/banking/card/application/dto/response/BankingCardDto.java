package com.damian.xBank.modules.banking.card.application.dto.response;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingCardDto(
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
}
