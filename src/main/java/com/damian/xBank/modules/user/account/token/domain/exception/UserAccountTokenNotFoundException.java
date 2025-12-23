package com.damian.xBank.modules.user.account.token.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountTokenNotFoundException extends UserAccountTokenException {
    public UserAccountTokenNotFoundException(Object resourceId) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_TOKEN_NOT_FOUND, resourceId);
    }

    public UserAccountTokenNotFoundException() {
        this("");
    }
}
