package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class SettingNotFoundException extends SettingException {

    public SettingNotFoundException(Long customerId) {
        super(ErrorCodes.SETTING_NOT_FOUND, customerId);
    }
}
