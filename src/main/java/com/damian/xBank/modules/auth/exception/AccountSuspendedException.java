package com.damian.whatsapp.modules.auth.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class AccountSuspendedException extends ApplicationException {
    public AccountSuspendedException(String message) {
        super(message);
    }
}
