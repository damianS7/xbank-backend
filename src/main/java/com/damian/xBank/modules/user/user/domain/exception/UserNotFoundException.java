package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String email) {
        super(ErrorCodes.USER_ACCOUNT_NOT_FOUND, email);
    }

    public UserNotFoundException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_NOT_FOUND, accountId);
    }
}
