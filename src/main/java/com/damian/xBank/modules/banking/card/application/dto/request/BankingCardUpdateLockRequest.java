package com.damian.xBank.modules.banking.card.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record BankingCardUpdateLockRequest(
        @NotNull(
                message = "Password must not be null"
        )
        String password
) {
}
