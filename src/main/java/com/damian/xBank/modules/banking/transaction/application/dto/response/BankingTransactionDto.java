package com.damian.xBank.modules.banking.transaction.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransactionDto(
        Long id,
        Long accountId,
        Long cardId,
        BigDecimal amount,
        BankingAccountCurrency currency,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        BankingTransactionType type,
        BankingTransactionStatus status,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
