package com.damian.xBank.modules.setting.application.usecase.get;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;

public record GetCurrentUserSettingsResult(
    UserSettings settings
) {
    public static GetCurrentUserSettingsResult from(Setting settings) {
        return new GetCurrentUserSettingsResult(settings.getSettings());
    }
}
