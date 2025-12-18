package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class UserAccountInvalidPasswordConfirmationException extends UserAccountException {
    public UserAccountInvalidPasswordConfirmationException(Long accountId) {
        super(Exceptions.USER_ACCOUNT_INVALID_PASSWORD, accountId);
    }
}
