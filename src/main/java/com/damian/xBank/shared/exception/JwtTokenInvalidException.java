package com.damian.xBank.shared.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenInvalidException extends AuthenticationException {
    public JwtTokenInvalidException() {
        super(ErrorCodes.AUTH_JWT_TOKEN_INVALID);
    }
}
