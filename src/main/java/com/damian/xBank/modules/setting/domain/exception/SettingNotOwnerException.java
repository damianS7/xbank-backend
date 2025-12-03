package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class SettingNotOwnerException extends ApplicationException {
    private final Long userId;

    public SettingNotOwnerException(String message) {
        this(message, null);
    }

    public SettingNotOwnerException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
