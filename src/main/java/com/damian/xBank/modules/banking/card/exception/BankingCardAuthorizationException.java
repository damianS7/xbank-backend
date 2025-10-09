package com.damian.xBank.modules.banking.card.exception;

import com.damian.xBank.modules.auth.exception.AuthorizationException;

public class BankingCardAuthorizationException extends AuthorizationException {
    public BankingCardAuthorizationException(String message) {
        super(message);
    }
}
