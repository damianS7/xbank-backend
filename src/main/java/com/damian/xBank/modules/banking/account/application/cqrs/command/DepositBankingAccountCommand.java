package com.damian.xBank.modules.banking.account.application.cqrs.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositBankingAccountCommand(
    Long bankingAccountId,

    @NotNull(message = "Depositor name must not be null")
    String depositorName,

    @Positive
    BigDecimal amount
) {
}
