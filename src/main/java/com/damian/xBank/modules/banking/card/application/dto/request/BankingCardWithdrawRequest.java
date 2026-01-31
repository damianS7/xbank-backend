package com.damian.xBank.modules.banking.card.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BankingCardWithdrawRequest(
        @Positive
        BigDecimal amount,

        @NotBlank
        @Size(min = 4, max = 4)
        String cardPIN
) {
}
