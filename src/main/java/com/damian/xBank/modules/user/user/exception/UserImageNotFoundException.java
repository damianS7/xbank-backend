package com.damian.whatsapp.modules.user.user.exception;

public class UserImageNotFoundException extends UserException {

    public UserImageNotFoundException(String message, Long userId) {
        super(message, userId);
    }
}
