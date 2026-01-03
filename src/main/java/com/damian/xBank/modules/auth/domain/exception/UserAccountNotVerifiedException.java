package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.modules.user.user.domain.exception.UserAccountException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountNotVerifiedException extends UserAccountException {
    public UserAccountNotVerifiedException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_NOT_VERIFIED, accountId);
    }

    public UserAccountNotVerifiedException(String email) {
        super(ErrorCodes.USER_ACCOUNT_NOT_VERIFIED, email);
    }
}
