package com.damian.xBank.modules.banking.card.application.cqrs.result;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;

public record CaptureCardPaymentResult(
    Long transactionId,
    BigDecimal amount,
    BigDecimal balanceBefore,
    BigDecimal balanceAfter,
    String description,
    BankingTransactionType type,
    BankingTransactionStatus status
) {
    public static CaptureCardPaymentResult from(BankingTransaction transaction) {
        return new CaptureCardPaymentResult(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getBalanceBefore(),
            transaction.getBalanceAfter(),
            transaction.getDescription(),
            transaction.getType(),
            transaction.getStatus()
        );
    }
}
