package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class UserAccountNotFoundException extends UserAccountException {

    public UserAccountNotFoundException(String email) {
        super(Exceptions.USER_ACCOUNT_NOT_FOUND, email);
    }

    public UserAccountNotFoundException(Long accountId) {
        super(Exceptions.USER_ACCOUNT_NOT_FOUND, accountId);
    }
}
