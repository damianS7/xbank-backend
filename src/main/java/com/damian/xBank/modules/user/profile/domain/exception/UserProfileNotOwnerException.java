package com.damian.xBank.modules.user.profile.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class UserProfileNotOwnerException extends UserProfileException {
    public UserProfileNotOwnerException(Long profileId) {
        super(ErrorCodes.PROFILE_NOT_OWNER, profileId);
    }

    public UserProfileNotOwnerException(Long profileId, Long userId) {
        super(ErrorCodes.PROFILE_NOT_OWNER, profileId, new Object[]{userId});
    }
}