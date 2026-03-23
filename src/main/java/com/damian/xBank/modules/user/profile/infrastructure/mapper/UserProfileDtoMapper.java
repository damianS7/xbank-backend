package com.damian.xBank.modules.user.profile.infrastructure.mapper;

import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateUserProfileCommand;
import com.damian.xBank.modules.user.profile.infrastructure.rest.request.UserProfileUpdateRequest;

public class UserProfileDtoMapper {
    public static UpdateUserProfileCommand toCommand(UserProfileUpdateRequest request) {
        return new UpdateUserProfileCommand(
            request.currentPassword(),
            request.fieldsToUpdate()
        );
    }
}
