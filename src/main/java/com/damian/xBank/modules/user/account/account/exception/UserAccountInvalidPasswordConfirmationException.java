package com.damian.whatsapp.modules.user.account.account.exception;

import com.damian.whatsapp.modules.user.user.exception.UserException;

public class UserAccountInvalidPasswordConfirmationException extends UserException {
    public UserAccountInvalidPasswordConfirmationException(String message, Long userId) {
        super(message, userId);
    }
}
