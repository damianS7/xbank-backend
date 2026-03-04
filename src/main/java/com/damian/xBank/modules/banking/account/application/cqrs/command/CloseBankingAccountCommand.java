package com.damian.xBank.modules.banking.account.application.cqrs.command;

import jakarta.validation.constraints.NotNull;

public record CloseBankingAccountCommand(
    Long accountId,
    
    @NotNull(
        message = "Password must not be null"
    )
    String password
) {
}
