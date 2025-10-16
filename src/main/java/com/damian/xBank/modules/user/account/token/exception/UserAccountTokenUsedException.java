package com.damian.xBank.modules.user.account.token.exception;

public class UserAccountTokenUsedException extends UserAccountTokenException {
    public UserAccountTokenUsedException(String message, String token, Long accountId) {
        super(message, token, accountId);
    }

    public UserAccountTokenUsedException(String message, String token, String email) {
        super(message, token, email);
    }
}
