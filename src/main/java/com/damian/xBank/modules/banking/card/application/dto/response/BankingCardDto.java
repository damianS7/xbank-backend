package com.damian.xBank.modules.banking.card.application.dto.response;

import com.damian.xBank.modules.banking.card.domain.enums.BankingCardLockStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BankingCardDto(
        Long id,
        Long bankingAccountId,
        String cardHolder,
        String cardNumber,
        String cardCVV,
        String cardPIN,
        BigDecimal dailyLimit,
        LocalDate expiredDate,
        BankingCardType cardType,
        BankingCardStatus cardStatus,
        BankingCardLockStatus lockStatus,
        Instant createdAt,
        Instant updatedAt
) {
}
