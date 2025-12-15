package com.damian.xBank.modules.setting.application.service;

import com.damian.xBank.modules.setting.UserSettings;
import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.domain.entity.Setting;
import com.damian.xBank.modules.setting.domain.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.infra.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SettingService {
    private static final Logger log = LoggerFactory.getLogger(SettingService.class);
    private final SettingRepository settingRepository;
    private final AuthenticationContext authenticationContext;

    public SettingService(
            SettingRepository settingRepository,
            AuthenticationContext authenticationContext
    ) {
        this.settingRepository = settingRepository;
        this.authenticationContext = authenticationContext;
    }

    // get all the settings for the current user
    public Setting getSettings() {
        User currentUser = authenticationContext.getCurrentUser();
        return settingRepository
                .findByUser_Id(currentUser.getId())
                .orElseThrow(
                        () -> new SettingNotFoundException(
                                Exceptions.CUSTOMER.SETTINGS.NOT_FOUND
                        )
                );
    }

    // Update settings for the current user
    public Setting updateSettings(SettingsUpdateRequest request) {
        User currentUser = authenticationContext.getCurrentUser();

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
    public void createDefaultSettings(UserAccount userAccount) {
        UserSettings defaultSettings = UserSettings.defaults();
        Setting setting = new Setting();
        setting.setUserAccount(userAccount);
        setting.setSettings(defaultSettings);
        settingRepository.save(setting);
    }
}
