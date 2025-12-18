package com.damian.xBank.shared.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenExpiredException extends AuthenticationException {
    public JwtTokenExpiredException() {
        super(Exceptions.JWT_TOKEN_EXPIRED);
    }
}
