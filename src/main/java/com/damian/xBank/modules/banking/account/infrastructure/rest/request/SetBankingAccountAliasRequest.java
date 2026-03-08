package com.damian.xBank.modules.banking.account.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;

public record SetBankingAccountAliasRequest(
    @NotNull(message = "Alias must not be null")
    String alias
) {
}
