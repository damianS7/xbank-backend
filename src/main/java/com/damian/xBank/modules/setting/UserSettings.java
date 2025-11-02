package com.damian.xBank.modules.setting;

public record UserSettings(
        boolean EMAIL_NOTIFICATIONS,
        boolean TWO_FACTOR_AUTHENTICATION,
        String TWO_FACTOR_CODE,
        String LANGUAGE
) {
    public static UserSettings defaults() {
        return new UserSettings(
                true,
                false,
                "",
                "EN"
        );
    }
}