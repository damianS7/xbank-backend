package com.damian.xBank.modules.user.profile.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserProfileUpdateException extends UserProfileException {

    public UserProfileUpdateException(Long customerId, Object[] args) {
        super(ErrorCodes.PROFILE_UPDATE_FAILED, customerId, args);
    }
}
