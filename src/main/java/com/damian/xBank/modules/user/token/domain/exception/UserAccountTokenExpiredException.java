package com.damian.xBank.modules.user.token.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserAccountTokenExpiredException extends UserAccountTokenException {
    public UserAccountTokenExpiredException(String email, String token) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_TOKEN_EXPIRED, email, new Object[]{token});
    }

    public UserAccountTokenExpiredException(Long accountId, String token) {
        super(ErrorCodes.USER_ACCOUNT_VERIFICATION_TOKEN_EXPIRED, accountId, new Object[]{token});
    }
}
