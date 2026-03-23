package com.damian.xBank.modules.banking.card.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;

public record ActivateBankingCardRequest(
    @NotNull(
        message = "cvv must not be null"
    )
    String cvv
) {
}
