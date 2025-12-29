package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.domain.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SettingGet {
    private static final Logger log = LoggerFactory.getLogger(SettingGet.class);
    private final AuthenticationContext authenticationContext;
    private final SettingRepository settingRepository;

    public SettingGet(
            AuthenticationContext authenticationContext,
            SettingRepository settingRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.settingRepository = settingRepository;
    }

    public Setting execute() {
        // User logged
        final User currentUser = authenticationContext.getCurrentUser();

        // find the settings for the currentUser
        return settingRepository
                .findByUser_Id(currentUser.getId())
                .orElseThrow(
                        () -> new SettingNotFoundException(currentUser.getId())
                );
    }
}