package com.damian.xBank.modules.user.account.account.domain.exception;

public class UserAccountInvalidPasswordConfirmationException extends UserAccountException {
    public UserAccountInvalidPasswordConfirmationException(String message, Long userId) {
        super(message, userId);
    }
}
