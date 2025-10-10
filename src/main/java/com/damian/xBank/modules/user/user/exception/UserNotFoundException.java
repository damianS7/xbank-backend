package com.damian.whatsapp.modules.user.user.exception;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String message, String username) {
        super(message, username);
    }

    public UserNotFoundException(String message, Long userId) {
        super(message, userId);
    }

}
