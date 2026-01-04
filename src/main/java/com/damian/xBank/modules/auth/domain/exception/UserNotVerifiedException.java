package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.modules.user.user.domain.exception.UserException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class UserNotVerifiedException extends UserException {
    public UserNotVerifiedException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_NOT_VERIFIED, accountId);
    }

    public UserNotVerifiedException(String email) {
        super(ErrorCodes.USER_ACCOUNT_NOT_VERIFIED, email);
    }
}
