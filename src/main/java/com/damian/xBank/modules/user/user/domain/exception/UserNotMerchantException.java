package com.damian.xBank.modules.user.user.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserNotMerchantException extends UserException {

    public UserNotMerchantException(Object resource) {
        super(ErrorCodes.USER_NOT_MERCHANT, resource);
    }
}
