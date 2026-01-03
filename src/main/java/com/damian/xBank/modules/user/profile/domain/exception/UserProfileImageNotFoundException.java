package com.damian.xBank.modules.user.profile.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserProfileImageNotFoundException extends UserProfileException {

    public UserProfileImageNotFoundException(Long customerId) {
        super(ErrorCodes.CUSTOMER_IMAGE_NOT_FOUND, customerId);
    }
}
