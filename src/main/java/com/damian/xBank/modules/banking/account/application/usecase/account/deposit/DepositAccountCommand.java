package com.damian.xBank.modules.banking.account.application.usecase.account.deposit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositAccountCommand(
    Long bankingAccountId,

    @NotNull(message = "Depositor name must not be null")
    String depositorName,

    @Positive
    BigDecimal amount
) {
}
