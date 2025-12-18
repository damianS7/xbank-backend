package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class SettingNotOwnerException extends SettingException {
    public SettingNotOwnerException(Long settingId, Long customerId) {
        super(Exceptions.SETTING_NOT_OWNER, settingId, new Object[]{settingId, customerId});
    }
}
