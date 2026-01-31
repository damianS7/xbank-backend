package com.damian.xBank.modules.banking.card.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record BankingCardActivateRequest(
        @NotNull(
                message = "cvv must not be null"
        )
        String cvv
) {
}
