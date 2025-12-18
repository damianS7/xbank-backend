package com.damian.xBank.modules.user.account.account.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class UserAccountVerificationNotPendingException extends UserAccountException {
    public UserAccountVerificationNotPendingException(Long accountId) {
        super(Exceptions.USER_ACCOUNT_VERIFICATION_NOT_ELEGIBLE, accountId);
    }

    public UserAccountVerificationNotPendingException(String email) {
        super(Exceptions.USER_ACCOUNT_VERIFICATION_NOT_ELEGIBLE, email);
    }
}
