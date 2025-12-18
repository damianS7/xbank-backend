package com.damian.xBank.modules.user.account.token.domain.exception;

import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountException;

public class UserAccountTokenException extends UserAccountException {

    public UserAccountTokenException(String message, Object resourceId) {
        super(message, resourceId, new Object[]{});
    }

    public UserAccountTokenException(String message, Object resourceId, Object[] args) {
        super(message, resourceId, args);
    }
}
