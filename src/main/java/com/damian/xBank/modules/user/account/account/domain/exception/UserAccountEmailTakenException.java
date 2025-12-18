package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class UserAccountEmailTakenException extends UserAccountException {
    public UserAccountEmailTakenException(String email) {
        super(Exceptions.USER_ACCOUNT_EMAIL_TAKEN, email);
    }

}
