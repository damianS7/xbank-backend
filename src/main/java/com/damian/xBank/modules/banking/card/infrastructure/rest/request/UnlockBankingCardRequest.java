package com.damian.xBank.modules.banking.card.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;

public record UnlockBankingCardRequest(
    @NotNull(
        message = "Password must not be null"
    )
    String password
) {
}
