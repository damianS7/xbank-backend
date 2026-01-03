package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class UserAccountException extends ApplicationException {

    public UserAccountException(String errorCode, Object resourceId) {
        super(errorCode, resourceId, new Object[]{});
    }

    public UserAccountException(String errorCode, Object resourceId, Object[] args) {
        super(errorCode, resourceId, args);
    }

}
