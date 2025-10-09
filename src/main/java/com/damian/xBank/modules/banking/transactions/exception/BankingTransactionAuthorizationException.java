package com.damian.xBank.modules.banking.transactions.exception;

import com.damian.xBank.modules.auth.exception.AuthorizationException;

public class BankingTransactionAuthorizationException extends AuthorizationException {
    public BankingTransactionAuthorizationException(String message) {
        super(message);
    }
}
