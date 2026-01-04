package com.damian.xBank.modules.user.user.domain.exception;

public class UserUpdateException extends UserException {
    private final String key;
    private final String value;

    public UserUpdateException(String message, Long userId, String key, String value) {
        super(message, userId);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
