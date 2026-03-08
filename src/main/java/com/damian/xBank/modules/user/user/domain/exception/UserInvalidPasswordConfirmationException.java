package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserInvalidPasswordConfirmationException extends UserException {
    public UserInvalidPasswordConfirmationException(Long accountId) {
        super(ErrorCodes.USER_INVALID_PASSWORD, accountId);
    }
}
