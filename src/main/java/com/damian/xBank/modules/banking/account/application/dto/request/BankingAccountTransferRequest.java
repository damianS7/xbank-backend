package com.damian.xBank.modules.banking.account.application.dto.request;

import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BankingAccountTransferRequest(
        @NotNull(message = "Transaction type must not be null")
        BankingTransactionType transactionType,

        @NotNull(message = "Banking account number must not be null")
        String toBankingAccountNumber,

        @NotNull(message = "Description must not be null")
        String description,

        @Positive
        BigDecimal amount,

        @NotNull(message = "Password must not be null")
        String password
) {
}
