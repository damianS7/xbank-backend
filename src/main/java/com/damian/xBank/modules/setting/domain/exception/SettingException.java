package com.damian.xBank.modules.setting.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class SettingException extends ApplicationException {

    public SettingException(String errorCode, Object resourceId) {
        super(errorCode, resourceId, new Object[]{resourceId});
    }

    public SettingException(String errorCode, Object resourceId, Object[] args) {
        super(errorCode, resourceId, args);
    }
}
