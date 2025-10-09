package com.damian.xBank.modules.auth.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class AuthorizationException extends ApplicationException {
    public AuthorizationException(String message) {
        super(message);
    }
}
