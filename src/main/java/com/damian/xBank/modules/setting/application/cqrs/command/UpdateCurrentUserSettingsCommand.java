package com.damian.xBank.modules.setting.application.cqrs.command;

import com.damian.xBank.modules.setting.domain.model.UserSettings;

public record UpdateCurrentUserSettingsCommand(
    UserSettings settings
) {
}
