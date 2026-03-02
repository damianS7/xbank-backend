package com.damian.xBank.modules.setting.infrastructure.mapper;

import com.damian.xBank.modules.setting.application.cqrs.command.UpdateCurrentUserSettingsCommand;
import com.damian.xBank.modules.setting.infrastructure.rest.dto.request.SettingsUpdateRequest;

public class SettingDtoMapper {
    public static UpdateCurrentUserSettingsCommand toCommand(SettingsUpdateRequest request) {
        return new UpdateCurrentUserSettingsCommand(request.settings());
    }
}
