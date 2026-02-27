package com.damian.xBank.modules.setting.infrastructure.mapper;

import com.damian.xBank.modules.setting.application.cqrs.command.SettingUpdateCommand;
import com.damian.xBank.modules.setting.infrastructure.rest.dto.request.SettingsUpdateRequest;

public class SettingDtoMapper {
    public static SettingUpdateCommand toCommand(SettingsUpdateRequest request) {
        return new SettingUpdateCommand(request.settings());
    }
}
