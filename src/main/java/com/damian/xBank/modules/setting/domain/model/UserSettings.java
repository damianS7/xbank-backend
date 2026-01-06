package com.damian.xBank.modules.setting.domain.model;

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