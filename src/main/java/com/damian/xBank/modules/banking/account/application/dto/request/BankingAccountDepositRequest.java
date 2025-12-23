package com.damian.xBank.modules.banking.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BankingAccountDepositRequest(
        @NotNull(message = "Depositor name must not be null")
        String depositorName,

        @Positive
        BigDecimal amount
) {
}
