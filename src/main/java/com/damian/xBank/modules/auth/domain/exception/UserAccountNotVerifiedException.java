package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountException;
import com.damian.xBank.shared.exception.Exceptions;

public class UserAccountNotVerifiedException extends UserAccountException {
    public UserAccountNotVerifiedException(Long accountId) {
        super(Exceptions.USER_ACCOUNT_NOT_VERIFIED, accountId);
    }

    public UserAccountNotVerifiedException(String email) {
        super(Exceptions.USER_ACCOUNT_NOT_VERIFIED, email);
    }
}
