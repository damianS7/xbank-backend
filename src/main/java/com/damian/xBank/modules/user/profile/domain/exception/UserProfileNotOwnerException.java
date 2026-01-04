package com.damian.xBank.modules.user.profile.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserProfileNotOwnerException extends UserProfileException {
    public UserProfileNotOwnerException(Long customerId) {
        super(ErrorCodes.PROFILE_UPDATE_FAILED, customerId);
    }
}
// TODO for removal