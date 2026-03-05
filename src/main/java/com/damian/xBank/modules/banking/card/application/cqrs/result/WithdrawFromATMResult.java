package com.damian.xBank.modules.banking.card.application.cqrs.result;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;

public record WithdrawFromATMResult(
    Long transactionId,
    Long bankingAccountId,
    Long bankingCardId,
    Long transferId,
    BigDecimal amount,
    BigDecimal balanceBefore,
    BigDecimal balanceAfter,
    String description,
    BankingTransactionType type,
    BankingTransactionStatus status
) {
    public static WithdrawFromATMResult from(BankingTransaction transaction) {
        return new WithdrawFromATMResult(
            transaction.getId(),
            transaction.getBankingAccountId(),
            transaction.getBankingCardId(),
            transaction.getTransferId(),
            transaction.getAmount(),
            transaction.getBalanceBefore(),
            transaction.getBalanceAfter(),
            transaction.getDescription(),
            transaction.getType(),
            transaction.getStatus()
        );
    }
}
