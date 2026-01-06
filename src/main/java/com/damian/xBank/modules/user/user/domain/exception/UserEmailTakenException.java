package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserEmailTakenException extends UserException {
    public UserEmailTakenException(String email) {
        super(ErrorCodes.USER_EMAIL_TAKEN, email);
    }

}
