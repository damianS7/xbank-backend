package com.damian.xBank.modules.user.token.domain.exception;

import com.damian.xBank.modules.user.user.domain.exception.UserException;

public class UserTokenException extends UserException {

    public UserTokenException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{});
    }

    public UserTokenException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }
}
