package com.damian.xBank.modules.banking.account.application.cqrs.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetBankingAccountAliasCommand(
    @Positive
    Long accountId,

    @NotNull(message = "Alias must not be null")
    String alias
) {
}
