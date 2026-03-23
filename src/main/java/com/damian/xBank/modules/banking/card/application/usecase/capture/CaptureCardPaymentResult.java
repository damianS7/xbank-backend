package com.damian.xBank.modules.banking.card.application.usecase.capture;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionPaymentStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;

import java.math.BigDecimal;

public record CaptureCardPaymentResult(
    Long transactionId,
    BigDecimal amount,
    BigDecimal balanceBefore,
    BigDecimal balanceAfter,
    String description,
    BankingTransactionType type,
    BankingTransactionPaymentStatus status
) {
    public static CaptureCardPaymentResult from(BankingTransaction transaction) {
        return new CaptureCardPaymentResult(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getBalanceBefore(),
            transaction.getBalanceAfter(),
            transaction.getDescription(),
            transaction.getType(),
            transaction.getPaymentStatus()
        );
    }
}
