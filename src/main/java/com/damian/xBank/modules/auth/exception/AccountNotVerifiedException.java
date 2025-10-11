package com.damian.xBank.modules.auth.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class AccountNotVerifiedException extends ApplicationException {
    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
