package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingTransactionStatusTransitionException extends BankingTransactionException {
    public BankingTransactionStatusTransitionException(Long transactionId) {
        super(Exceptions.BANKING.TRANSACTION.INVALID_STATUS_CHANGE, transactionId);
    }

    public BankingTransactionStatusTransitionException(String message, Long transactionId) {
        super(message, transactionId);
    }
}
