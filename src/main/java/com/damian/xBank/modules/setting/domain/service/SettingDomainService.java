package com.damian.xBank.modules.setting.domain.service;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class SettingDomainService {
    public SettingDomainService() {
    }

    /**
     * Create default settings for the user
     *
     * @param user
     * @return default settings for the user
     */
    public Setting initializeDefaultSettingsFor(User user) {
        UserSettings defaultSettings = UserSettings.defaults();

        return Setting.create(user)
                      .setSettings(defaultSettings);
    }
}
