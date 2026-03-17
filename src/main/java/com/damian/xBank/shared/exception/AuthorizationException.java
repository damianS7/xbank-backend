package com.damian.xBank.shared.exception;

public class AuthorizationException extends ApplicationException {
    public AuthorizationException() {
        super(ErrorCodes.AUTHORIZATION, null, null);
    }
}