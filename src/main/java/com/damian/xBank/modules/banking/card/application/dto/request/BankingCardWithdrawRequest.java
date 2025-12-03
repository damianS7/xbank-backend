package com.damian.xBank.modules.banking.card.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BankingCardWithdrawRequest(
        @Positive
        BigDecimal amount,

        @NotNull(
                message = "card pin must not be null"
        )
        String cardPIN
) {
}
