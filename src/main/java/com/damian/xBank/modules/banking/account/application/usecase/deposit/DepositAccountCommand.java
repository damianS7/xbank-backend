package com.damian.xBank.modules.banking.account.application.usecase.deposit;

import java.math.BigDecimal;

public record DepositAccountCommand(
    Long bankingAccountId,
    String depositorName,
    BigDecimal amount
) {
}
