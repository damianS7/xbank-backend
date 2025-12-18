package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountEmailTakenException extends UserAccountException {
    public UserAccountEmailTakenException(String email) {
        super(ErrorCodes.USER_ACCOUNT_EMAIL_TAKEN, email);
    }

}
