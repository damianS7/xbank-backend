package com.damian.xBank.modules.banking.transaction.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record BankingTransactionConfirmRequest(
        @NotNull(
                message = "Password must not be null"
        ) String password
) {
}
