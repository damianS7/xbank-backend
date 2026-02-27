package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.cqrs.command.SettingUpdateCommand;
import com.damian.xBank.modules.setting.application.cqrs.result.SettingResult;
import com.damian.xBank.modules.setting.domain.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingUpdate {
    private static final Logger log = LoggerFactory.getLogger(SettingUpdate.class);
    private final AuthenticationContext authenticationContext;
    private final SettingRepository settingRepository;

    public SettingUpdate(
        AuthenticationContext authenticationContext,
        SettingRepository settingRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.settingRepository = settingRepository;
    }

    /**
     * It updates the settings for the current user
     *
     * @param command settings to update
     * @return updated settings
     */
    @Transactional
    public SettingResult execute(SettingUpdateCommand command) {
        // User logged
        final User currentUser = authenticationContext.getCurrentUser();

        // find the settings for the currentUser
        Setting userSettings = settingRepository
            .findByUser_Id(currentUser.getId())
            .orElseThrow(
                () -> new SettingNotFoundException(currentUser.getId())
            );

        // check if the logged user is the owner of the setting.
        userSettings.assertOwnedBy(currentUser.getId());

        // update settings
        userSettings.setSettings(command.settings());

        // Save
        settingRepository.save(userSettings);

        log.debug(
            "Updated settings: {} by user: {}",
            userSettings.getSettings().toString(),
            currentUser.getId()
        );

        return new SettingResult(userSettings.getSettings());
    }
}