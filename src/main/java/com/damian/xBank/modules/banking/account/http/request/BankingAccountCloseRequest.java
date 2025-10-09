package com.damian.xBank.modules.banking.account.http.request;

import jakarta.validation.constraints.NotNull;

public record BankingAccountCloseRequest(
        @NotNull(
                message = "Password must not be null"
        )
        String password
) {
}
