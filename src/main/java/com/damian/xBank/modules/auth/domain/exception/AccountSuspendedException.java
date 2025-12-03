package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class AccountSuspendedException extends ApplicationException {
    public AccountSuspendedException(String message) {
        super(message);
    }
}
