package com.damian.whatsapp.modules.user.account.account.exception;

public class UserAccountNotFoundException extends UserAccountException {

    public UserAccountNotFoundException(String message, String email) {
        super(message, email);
    }

    public UserAccountNotFoundException(String message, Long accountId) {
        super(message, accountId);
    }
}
