package com.damian.xBank.modules.banking.transaction.dto.response;

import com.damian.xBank.modules.banking.account.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransactionDto(
        Long id,
        Long accountId,
        Long cardId,
        BigDecimal amount,
        BankingAccountCurrency currency,
        BigDecimal lastBalance,
        BankingTransactionType transactionType,
        BankingTransactionStatus transactionStatus,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
