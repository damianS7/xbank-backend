package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class SettingNotFoundException extends SettingException {

    public SettingNotFoundException(Long customerId) {
        super(Exceptions.SETTING_NOT_FOUND, customerId);
    }
}
