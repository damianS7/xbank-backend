package com.damian.xBank.modules.setting.domain.service;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class SettingDomainService {
    public SettingDomainService() {
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
