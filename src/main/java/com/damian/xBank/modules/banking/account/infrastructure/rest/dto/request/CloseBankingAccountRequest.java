package com.damian.xBank.modules.banking.account.infrastructure.rest.dto.request;

import jakarta.validation.constraints.NotNull;

public record CloseBankingAccountRequest(
    @NotNull(
        message = "Password must not be null"
    )
    String password
) {
}
