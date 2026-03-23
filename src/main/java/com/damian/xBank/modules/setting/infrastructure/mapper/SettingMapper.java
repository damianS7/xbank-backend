package com.damian.xBank.modules.setting.infrastructure.mapper;

import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsQuery;
import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettingsCommand;
import com.damian.xBank.modules.setting.infrastructure.rest.request.UpdateCurrentUserSettingsRequest;

public class SettingMapper {
    public static UpdateCurrentUserSettingsCommand toCommand(UpdateCurrentUserSettingsRequest request) {
        return new UpdateCurrentUserSettingsCommand(request.settings());
    }

    public static GetCurrentUserSettingsQuery toGetCurrentUserSettingsQuery() {
        return new GetCurrentUserSettingsQuery();
    }
}
