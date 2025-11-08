package com.damian.xBank.modules.setting;

import com.damian.xBank.modules.setting.enums.SettingLanguage;
import com.damian.xBank.modules.setting.enums.SettingMultifactor;
import com.damian.xBank.modules.setting.enums.SettingTheme;

public record UserSettings(
        boolean appNotifications,
        boolean emailNotifications,
        boolean multifactor,
        boolean signOperations,
        String signOperationsPIN,
        int sessionTimeout,
        SettingMultifactor multifactorMethod,
        SettingLanguage language,
        SettingTheme theme
) {
    public static UserSettings defaults() {
        return new UserSettings(
                true,
                true,
                false,
                false,
                "0000",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.EN,
                SettingTheme.LIGHT
        );
    }
}