package com.damian.xBank.modules.banking.card.exception;


import com.damian.xBank.shared.exception.ApplicationException;

public class BankingCardAuthorizationException extends ApplicationException {
    public BankingCardAuthorizationException(String message) {
        super(message);
    }
}
