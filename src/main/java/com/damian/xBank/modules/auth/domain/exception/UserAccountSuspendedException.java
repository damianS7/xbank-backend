package com.damian.xBank.modules.auth.domain.exception;

import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountSuspendedException extends UserAccountException {
    public UserAccountSuspendedException(Long accountId) {
        super(ErrorCodes.USER_ACCOUNT_SUSPENDED, accountId);
    }

    public UserAccountSuspendedException(String email) {
        super(ErrorCodes.USER_ACCOUNT_SUSPENDED, email);
    }
}
