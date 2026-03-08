package com.damian.xBank.modules.setting.application.usecase.update;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;

public record UpdateCurrentUserSettingsResult(
    UserSettings settings
) {
    public static UpdateCurrentUserSettingsResult from(Setting settings) {
        return new UpdateCurrentUserSettingsResult(settings.getSettings());
    }
}
