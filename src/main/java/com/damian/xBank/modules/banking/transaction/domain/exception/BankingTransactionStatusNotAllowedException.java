package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingTransactionStatusNotAllowedException extends BankingTransactionException {
    public BankingTransactionStatusNotAllowedException(Long transactionId) {
        super(Exceptions.BANKING.TRANSACTION.INVALID_STATUS_CHANGE, transactionId);
    }

    public BankingTransactionStatusNotAllowedException(String message, Long transactionId) {
        super(message, transactionId);
    }
}
