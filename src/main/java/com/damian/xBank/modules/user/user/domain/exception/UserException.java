package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;

public class UserException extends ApplicationException {

    public UserException(String errorCode, Object resourceId) {
        super(errorCode, resourceId, new Object[]{});
    }

    public UserException(String errorCode, Object resourceId, Object[] args) {
        super(errorCode, resourceId, args);
    }

}
