package com.damian.xBank.modules.setting.service;

import com.damian.xBank.modules.setting.UserSettings;
import com.damian.xBank.modules.setting.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.repository.SettingRepository;
import com.damian.xBank.shared.domain.Setting;
import com.damian.xBank.shared.domain.User;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
    private static final Logger log = LoggerFactory.getLogger(SettingService.class);
    private final SettingRepository settingRepository;

    public SettingService(
            SettingRepository settingRepository
    ) {
        this.settingRepository = settingRepository;
    }

    // get all the settings for the current user
    public Setting getSettings() {
        User currentUser = AuthHelper.getCurrentUser();
        return settingRepository.findByUser_Id(currentUser.getId())
                                .orElseThrow(
                                        () -> new SettingNotFoundException(
                                                Exceptions.CUSTOMER.SETTINGS.NOT_FOUND
                                        )
                                );
    }

    // Update settings for the current user
    public Setting updateSettings(SettingsUpdateRequest request) {
        User currentUser = AuthHelper.getCurrentUser();

        // find the setting by settingId
        Setting userSettings = settingRepository
                .findByUser_Id(currentUser.getId())
                .orElseThrow(
                        () -> new SettingNotFoundException(Exceptions.CUSTOMER.SETTINGS.NOT_FOUND, currentUser.getId())
                );

        // check if the logged user is the owner of the setting.
        if (!userSettings.isOwner(currentUser)) {
            throw new SettingNotOwnerException(Exceptions.CUSTOMER.SETTINGS.NOT_OWNER, currentUser.getId());
        }

        log.debug(
                "Updated settings: {} by user: {}",
                request.settings().toString(),
                currentUser.getId()
        );

        userSettings.setSettings(request.settings());
        return settingRepository.save(userSettings);
    }

    // TODO
    public void createDefaultSettingsForUser(UserAccount userAccount) {
        UserSettings defaultSettings = UserSettings.defaults();
        Setting setting = new Setting();
        setting.setUserAccount(userAccount);
        setting.setSettings(defaultSettings);
        settingRepository.save(setting);
    }
}
