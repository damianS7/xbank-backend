package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.modules.auth.exception.AuthorizationException;

public class BankingAccountAuthorizationException extends AuthorizationException {
    public BankingAccountAuthorizationException(String message) {
        super(message);
    }

}
