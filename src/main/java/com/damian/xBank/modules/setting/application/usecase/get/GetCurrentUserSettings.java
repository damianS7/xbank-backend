package com.damian.xBank.modules.setting.application.usecase.get;

import com.damian.xBank.modules.setting.domain.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para obtener las settings del usuario actual.
 */
@Service
public class GetCurrentUserSettings {
    private static final Logger log = LoggerFactory.getLogger(GetCurrentUserSettings.class);
    private final AuthenticationContext authenticationContext;
    private final SettingRepository settingRepository;

    public GetCurrentUserSettings(
        AuthenticationContext authenticationContext,
        SettingRepository settingRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.settingRepository = settingRepository;
    }

    /**
     * @return Settings del usuario actual
     */
    public GetCurrentUserSettingsResult execute(GetCurrentUserSettingsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Buscar las settings del usuario
        Setting userSettings = settingRepository
            .findByUser_Id(currentUser.getId())
            .orElseThrow(
                () -> new SettingNotFoundException(currentUser.getId())
            );

        return GetCurrentUserSettingsResult.from(userSettings);
    }
}