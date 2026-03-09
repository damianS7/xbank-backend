package com.damian.xBank.modules.user.profile.application.usecase.update;

import java.util.Map;

public record UpdateUserProfileCommand(
    String currentPassword,
    Map<String, Object> fieldsToUpdate
) {
}
