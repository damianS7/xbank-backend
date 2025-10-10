package com.damian.whatsapp.modules.setting.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

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
