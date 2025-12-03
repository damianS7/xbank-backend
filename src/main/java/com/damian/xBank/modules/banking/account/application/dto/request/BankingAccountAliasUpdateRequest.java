package com.damian.xBank.modules.banking.account.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record BankingAccountAliasUpdateRequest(
        @NotNull(message = "Alias must not be null")
        String alias
) {
}
