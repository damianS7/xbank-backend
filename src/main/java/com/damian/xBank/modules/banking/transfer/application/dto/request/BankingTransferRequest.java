package com.damian.xBank.modules.banking.transfer.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BankingTransferRequest(

        @Positive
        Long fromAccountId,

        @NotNull(message = "Banking account number must not be null")
        String toAccountNumber,

        @NotNull(message = "Description must not be null")
        String description,

        @Positive
        BigDecimal amount
) {
}
