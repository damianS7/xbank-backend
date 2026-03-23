package com.damian.xBank.modules.banking.transaction.application.dto;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransactionResult(
    Long id,
    Long bankingAccountId,
    Long bankingCardId,
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
    public static BankingTransactionResult from(BankingTransaction transaction) {
        return new BankingTransactionResult(
            transaction.getId(),
            transaction.getBankingAccount().getId(),
            transaction.getBankingCard() != null ? transaction.getBankingCard().getId() : null,
            transaction.getAmount(),
            transaction.getBankingAccount().getCurrency(),
            transaction.getBalanceBefore(),
            transaction.getBalanceAfter(),
            transaction.getType(),
            transaction.getStatus(),
            transaction.getDescription(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}
