package com.damian.xBank.modules.setting.domain.service;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
    public SettingService() {
    }

    /**
     * Update settings for the given user
     *
     * @param userId
     * @param settings
     * @param newSettings
     */
    public void updateSettings(Long userId, Setting settings, UserSettings newSettings) {
        // check if the logged user is the owner of the setting.
        settings.assertOwnedBy(userId);

        settings.setSettings(newSettings);
    }

    /**
     * Create default settings for the user
     *
     * @param userAccount
     * @return default settings for the user
     */
    public Setting initializeDefaultSettingsFor(UserAccount userAccount) {
        UserSettings defaultSettings = UserSettings.defaults();

        return Setting.create(userAccount)
                      .setSettings(defaultSettings);
    }
}
