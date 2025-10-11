package com.damian.xBank.modules.auth.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class AccountSuspendedException extends ApplicationException {
    public AccountSuspendedException(String message) {
        super(message);
    }
}
