package com.damian.xBank.modules.banking.transfer.application.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BankingTransferRequest(

        @Positive
        Long fromAccountId,

        @Positive
        Long toAccountId,

        @Positive
        BigDecimal amount
) {
}
