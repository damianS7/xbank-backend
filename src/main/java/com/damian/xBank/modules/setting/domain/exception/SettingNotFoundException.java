package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class SettingNotFoundException extends ApplicationException {
    private final Long settingId;

    public SettingNotFoundException(String message) {
        this(message, null);
    }

    public SettingNotFoundException(String message, Long settingId) {
        super(message);
        this.settingId = settingId;

    }

    public Long getSettingId() {
        return settingId;
    }
}
