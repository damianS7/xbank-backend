package com.damian.whatsapp.modules.user.user.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class UserException extends ApplicationException {
    private final Long userId;
    private final String username;

    public UserException(String message, String username) {
        super(message);
        this.username = username;
        this.userId = null;
    }

    public UserException(String message, Long userId) {
        super(message);
        this.userId = userId;
        this.username = null;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
