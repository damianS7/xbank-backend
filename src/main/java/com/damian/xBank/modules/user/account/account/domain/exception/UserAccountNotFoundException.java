package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountNotFoundException extends UserAccountException {

    public UserAccountNotFoundException(String email) {
        super(ErrorCodes.USER_ACCOUNT_NOT_FOUND, email);
    }

    public UserAccountNotFoundException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_NOT_FOUND, accountId);
    }
}
