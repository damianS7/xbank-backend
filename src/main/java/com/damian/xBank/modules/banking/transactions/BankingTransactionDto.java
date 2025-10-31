package com.damian.xBank.modules.banking.transactions;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransactionDto(
        Long id,
        Long accountId,
        Long cardId,
        BigDecimal amount,
        BigDecimal lastBalance,
        BankingTransactionType transactionType,
        BankingTransactionStatus transactionStatus,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
