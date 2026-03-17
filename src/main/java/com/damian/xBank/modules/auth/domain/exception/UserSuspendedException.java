package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.modules.user.user.domain.exception.UserException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class UserSuspendedException extends UserException {
    public UserSuspendedException(Object resource) {
        super(ErrorCodes.USER_SUSPENDED, resource);
    }
}
