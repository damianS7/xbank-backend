package com.damian.xBank.modules.setting.dto.response;

import com.damian.xBank.modules.setting.UserSettings;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record SettingDto(
        @JsonUnwrapped
        UserSettings settings
) {
}
