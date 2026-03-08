package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class SettingNotOwnerException extends SettingException {
    public SettingNotOwnerException(Long settingId, Long userId) {
        super(ErrorCodes.SETTING_NOT_OWNER, settingId, new Object[]{settingId, userId});
    }
}
