package com.damian.whatsapp.modules.user.user.exception;

public class UserUpdateException extends UserException {
    public UserUpdateException(String message, Long userId) {
        super(message, userId);
    }
}
