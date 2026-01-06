package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserVerificationNotPendingException extends UserException {
    public UserVerificationNotPendingException(Long accountId) {
        super(ErrorCodes.USER_VERIFICATION_NOT_PENDING, accountId);
    }

    public UserVerificationNotPendingException(String email) {
        super(ErrorCodes.USER_VERIFICATION_NOT_PENDING, email);
    }
}
