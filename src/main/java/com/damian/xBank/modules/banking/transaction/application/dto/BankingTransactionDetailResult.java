package com.damian.xBank.modules.banking.transaction.application.dto;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransactionDetailResult(
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
    public static BankingTransactionDetailResult from(BankingTransaction transaction) {
        return new BankingTransactionDetailResult(
            transaction.getId(),
            transaction.getBankingAccount().getId(),
            transaction.getOutgoingTransfer() != null ? transaction
                .getOutgoingTransfer()
                .getFromAccount()
                .getOwner()
                .getProfile()
                .getFullName() : null,
            transaction.getOutgoingTransfer() != null ? transaction
                .getOutgoingTransfer()
                .getFromAccount()
                .getAccountNumber() : null,
            transaction.getOutgoingTransfer() != null ? transaction
                .getOutgoingTransfer()
                .getToAccount()
                .getOwner()
                .getProfile()
                .getFullName() : null,
            transaction.getOutgoingTransfer() != null ? transaction.getOutgoingTransfer()
                .getToAccount()
                .getAccountNumber() : null,
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
