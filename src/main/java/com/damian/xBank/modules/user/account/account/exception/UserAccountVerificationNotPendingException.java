package com.damian.xBank.modules.user.account.account.exception;

public class UserAccountVerificationNotPendingException extends UserAccountException {
    public UserAccountVerificationNotPendingException(String message, Long accountId) {
        super(message, accountId);
    }

    public UserAccountVerificationNotPendingException(String message, String email) {
        super(message, email);
    }
}
