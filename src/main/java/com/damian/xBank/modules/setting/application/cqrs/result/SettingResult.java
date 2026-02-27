package com.damian.xBank.modules.setting.application.cqrs.result;

import com.damian.xBank.modules.setting.domain.model.UserSettings;

public record SettingResult(
    UserSettings settings
) {
}
