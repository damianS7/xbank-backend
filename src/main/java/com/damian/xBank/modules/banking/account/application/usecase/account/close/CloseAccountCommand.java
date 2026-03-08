package com.damian.xBank.modules.banking.account.application.usecase.account.close;

import jakarta.validation.constraints.NotNull;

public record CloseAccountCommand(
    Long accountId,

    @NotNull(
        message = "Password must not be null"
    )
    String password
) {
}
