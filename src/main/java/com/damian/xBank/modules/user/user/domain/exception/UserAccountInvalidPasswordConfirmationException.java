package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountInvalidPasswordConfirmationException extends UserAccountException {
    public UserAccountInvalidPasswordConfirmationException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD, accountId);
    }
}
