package com.damian.xBank.modules.banking.transactions.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingTransactionException extends ApplicationException {
    public BankingTransactionException(String message) {
        super(message);
    }
}
