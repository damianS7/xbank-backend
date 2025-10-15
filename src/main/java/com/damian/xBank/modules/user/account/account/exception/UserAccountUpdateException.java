package com.damian.xBank.modules.user.account.account.exception;

public class UserAccountUpdateException extends UserAccountException {
    private final String key;
    private final String value;

    public UserAccountUpdateException(String message, Long userId, String key, String value) {
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
