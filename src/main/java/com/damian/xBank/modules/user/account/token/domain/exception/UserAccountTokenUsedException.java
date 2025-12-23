package com.damian.xBank.modules.user.account.token.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountTokenUsedException extends UserAccountTokenException {
    public UserAccountTokenUsedException(Long accountId, String token) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_TOKEN_USED, accountId, new Object[]{token});
    }

    public UserAccountTokenUsedException(String email, String token) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_TOKEN_USED, email, new Object[]{token});
    }
}
