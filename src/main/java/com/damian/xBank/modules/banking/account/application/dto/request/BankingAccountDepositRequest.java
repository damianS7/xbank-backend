package com.damian.xBank.modules.banking.account.application.dto.request;

import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BankingAccountDepositRequest(
        @NotNull(message = "Transaction type must not be null")
        BankingTransactionType transactionType,

        @NotNull(message = "Depositor name must not be null")
        String depositorName,

        @Positive
        BigDecimal amount
) {
}
