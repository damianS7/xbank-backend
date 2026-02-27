package com.damian.xBank.modules.user.profile.application.cqrs.command;

import java.util.Map;

public record UserProfileUpdateCommand(
    String currentPassword,
    Map<String, Object> fieldsToUpdate
) {
}
