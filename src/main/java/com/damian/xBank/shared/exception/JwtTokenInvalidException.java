package com.damian.xBank.shared.exception;

import org.springframework.security.core.AuthenticationException;

// Remove and use ExpiredJwtException instead
public class JwtTokenInvalidException extends AuthenticationException {
    public JwtTokenInvalidException() {
        super(Exceptions.JWT_TOKEN_INVALID);
    }
}
