package com.damian.xBank.modules.banking.account.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositBankingAccountRequest(
    @NotNull(message = "Depositor name must not be null")
    String depositorName,

    @Positive
    BigDecimal amount
) {
}
