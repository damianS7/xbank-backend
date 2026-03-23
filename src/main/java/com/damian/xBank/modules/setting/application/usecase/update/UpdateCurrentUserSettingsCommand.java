package com.damian.xBank.modules.setting.application.usecase.update;

import com.damian.xBank.modules.setting.domain.model.UserSettings;

public record UpdateCurrentUserSettingsCommand(
    UserSettings settings
) {
}
