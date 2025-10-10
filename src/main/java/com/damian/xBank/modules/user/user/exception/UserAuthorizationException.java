package com.damian.whatsapp.modules.user.user.exception;

public class UserAuthorizationException extends UserException {
    public UserAuthorizationException(String message, Long userId) {
        super(message, userId);
    }
}
