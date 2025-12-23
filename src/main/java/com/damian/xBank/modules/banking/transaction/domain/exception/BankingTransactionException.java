package com.damian.xBank.modules.banking.transaction.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingTransactionException extends ApplicationException {
    public BankingTransactionException(String message, Object resourceId) {
        this(message, resourceId, new Object[]{});
    }

    public BankingTransactionException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }
}
