package com.damian.xBank.modules.banking.account.application.usecase.set.alias;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetAccountAliasCommand(
    @Positive
    Long accountId,

    @NotNull(message = "Alias must not be null")
    String alias
) {
}
