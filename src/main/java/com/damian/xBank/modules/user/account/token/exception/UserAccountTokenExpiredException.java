package com.damian.xBank.modules.user.account.token.exception;

public class UserAccountTokenExpiredException extends UserAccountTokenException {
    public UserAccountTokenExpiredException(String message, String token, String email) {
        super(message, token, email);
    }

    public UserAccountTokenExpiredException(String message, String token, Long userId) {
        super(message, token, userId);
    }
}
