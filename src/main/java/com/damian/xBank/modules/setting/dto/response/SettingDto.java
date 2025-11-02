package com.damian.xBank.modules.setting.dto.response;

import com.damian.xBank.modules.setting.UserSettings;
import jakarta.validation.constraints.NotNull;

public record SettingDto(
        @NotNull(message = "Settings cannot be null")
        UserSettings settings
) {
}
