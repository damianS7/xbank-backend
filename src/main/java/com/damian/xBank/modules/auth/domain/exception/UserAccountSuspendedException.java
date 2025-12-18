package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountException;
import com.damian.xBank.shared.exception.Exceptions;

public class UserAccountSuspendedException extends UserAccountException {
    public UserAccountSuspendedException(Long accountId) {
        super(Exceptions.USER_ACCOUNT_SUSPENDED, accountId);
    }

    public UserAccountSuspendedException(String email) {
        super(Exceptions.USER_ACCOUNT_SUSPENDED, email);
    }
}
