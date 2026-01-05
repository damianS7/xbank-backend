package com.damian.xBank.modules.user.token.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserTokenUsedException extends UserTokenException {
    public UserTokenUsedException(Long accountId, String token) {
        super(ErrorCodes.USER_TOKEN_USED, accountId, new Object[]{token});
    }

    public UserTokenUsedException(String email, String token) {
        super(ErrorCodes.USER_TOKEN_USED, email, new Object[]{token});
    }
}
