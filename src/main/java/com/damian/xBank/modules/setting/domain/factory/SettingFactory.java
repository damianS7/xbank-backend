package com.damian.xBank.modules.setting.domain.factory;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import org.springframework.stereotype.Component;

@Component
public class SettingFactory {

    public Setting createDefault() {
        Setting setting = new Setting();
        setting.setSettings(UserSettings.defaults());
        return setting;
    }
}