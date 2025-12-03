package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class BankingTransactionNotFoundException extends BankingTransactionException {
    public BankingTransactionNotFoundException(Long transactionId) {
        super(Exceptions.BANKING.TRANSACTION.NOT_FOUND, transactionId);
    }

    public BankingTransactionNotFoundException(String message, Long transactionId) {
        super(message, transactionId);
    }
}
