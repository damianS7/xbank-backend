package com.damian.xBank.modules.user.account.token.exception;

import com.damian.xBank.modules.user.account.account.exception.UserAccountException;

public class UserAccountTokenException extends UserAccountException {
    private final String token;

    public UserAccountTokenException(String message, String token, Long accountId) {
        super(message, accountId);
        this.token = token;
    }

    public UserAccountTokenException(String message, String token, String email) {
        super(message, email);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
