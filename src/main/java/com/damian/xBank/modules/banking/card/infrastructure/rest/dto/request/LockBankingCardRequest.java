package com.damian.xBank.modules.banking.card.infrastructure.rest.dto.request;

import jakarta.validation.constraints.NotNull;

public record LockBankingCardRequest(
    @NotNull(
        message = "Password must not be null"
    )
    String password
) {
}
