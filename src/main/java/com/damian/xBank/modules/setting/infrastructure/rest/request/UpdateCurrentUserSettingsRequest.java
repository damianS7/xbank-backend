package com.damian.xBank.modules.setting.infrastructure.rest.request;

import com.damian.xBank.modules.setting.domain.model.UserSettings;
import jakarta.validation.constraints.NotNull;

/**
 * Request used to update settings.
 *
 * @param settings all the settings to update
 */
public record UpdateCurrentUserSettingsRequest(
    @NotNull(message = "Settings cannot be null")
    UserSettings settings
) {

}
