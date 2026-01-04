package com.damian.xBank.modules.user.token.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserTokenNotFoundException extends UserTokenException {
    public UserTokenNotFoundException(Object resourceId) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_TOKEN_NOT_FOUND, resourceId);
    }

    public UserTokenNotFoundException() {
        this("");
    }
}
