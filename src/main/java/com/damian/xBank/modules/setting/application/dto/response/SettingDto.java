package com.damian.xBank.modules.setting.application.dto.response;

import com.damian.xBank.modules.setting.domain.model.UserSettings;
import jakarta.validation.constraints.NotNull;

public record SettingDto(
        @NotNull(message = "Settings cannot be null")
        UserSettings settings
) {
}
