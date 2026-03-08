package com.damian.xBank.modules.user.profile.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class UserProfileException extends ApplicationException {
    public UserProfileException(String errorCode, Object resourceId) {
        this(errorCode, resourceId, new Object[]{resourceId});
    }

    public UserProfileException(String errorCode, Object resourceId, Object[] args) {
        super(errorCode, resourceId, args);
    }
}
