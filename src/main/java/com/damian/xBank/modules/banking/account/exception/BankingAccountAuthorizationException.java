package com.damian.xBank.modules.banking.account.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class BankingAccountAuthorizationException extends ApplicationException {
    public BankingAccountAuthorizationException(String message) {
        super(message);
    }

}
