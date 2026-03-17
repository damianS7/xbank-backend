package com.damian.xBank.modules.setting.application.usecase.update;

import com.damian.xBank.modules.setting.domain.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para actualizar las settings del usuario actual.
 */
@Service
public class UpdateCurrentUserSettings {
    private static final Logger log = LoggerFactory.getLogger(UpdateCurrentUserSettings.class);
    private final AuthenticationContext authenticationContext;
    private final SettingRepository settingRepository;

    public UpdateCurrentUserSettings(
        AuthenticationContext authenticationContext,
        SettingRepository settingRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.settingRepository = settingRepository;
    }

    /**
     *
     * @param command Comando con los datos a actualizar.
     * @return Las settings actualizadas.
     */
    @Transactional
    public UpdateCurrentUserSettingsResult execute(UpdateCurrentUserSettingsCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Buscar las settings del usuario
        Setting userSettings = settingRepository
            .findByUser_Id(currentUser.getId())
            .orElseThrow(
                () -> new SettingNotFoundException(currentUser.getId())
            );

        // Actualizar las settings
        userSettings.setSettings(command.settings());
        settingRepository.save(userSettings);

        log.debug(
            "Updated settings: {} by user: {}",
            userSettings.getSettings().toString(),
            currentUser.getId()
        );

        return UpdateCurrentUserSettingsResult.from(userSettings);
    }
}