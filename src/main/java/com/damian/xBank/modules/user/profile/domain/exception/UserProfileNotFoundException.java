package com.damian.xBank.modules.user.profile.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserProfileNotFoundException extends UserProfileException {
    public UserProfileNotFoundException(Long userId) {
        super(ErrorCodes.CUSTOMER_NOT_FOUND, userId);
    }
}
