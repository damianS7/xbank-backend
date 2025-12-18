package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountVerificationNotPendingException extends UserAccountException {
    public UserAccountVerificationNotPendingException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_NOT_PENDING, accountId);
    }

    public UserAccountVerificationNotPendingException(String email) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_NOT_PENDING, email);
    }
}
