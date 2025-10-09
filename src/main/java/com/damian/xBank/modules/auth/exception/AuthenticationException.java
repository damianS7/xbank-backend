package com.damian.xBank.modules.auth.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(message);
    }
}
