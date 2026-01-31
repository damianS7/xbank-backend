package com.damian.xBank.modules.banking.transaction.application.dto.response;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransactionDetailDto(
        Long id,
        Long accountId,
        String fromUser,
        String fromAccountNumber,
        String toUser,
        String toAccountNumber,
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
