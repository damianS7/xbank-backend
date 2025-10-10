package com.damian.whatsapp.modules.user.account.account.exception;

public class UserAccountEmailTakenException extends UserAccountException {
    public UserAccountEmailTakenException(String message, String email) {
        super(message, email);
    }

    public UserAccountEmailTakenException(String message, Long accountId) {
        super(message, accountId);
    }
}
