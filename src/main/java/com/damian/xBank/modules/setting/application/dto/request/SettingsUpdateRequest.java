package com.damian.xBank.modules.setting.application.dto.request;

import com.damian.xBank.modules.setting.UserSettings;
import jakarta.validation.constraints.NotNull;

/**
 * Request used to update settings.
 *
 * @param settings all the settings to update
 */
public record SettingsUpdateRequest(
        @NotNull(message = "Settings cannot be null")
        UserSettings settings
) {

}
