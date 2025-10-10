package com.damian.whatsapp.modules.setting.service;

import com.damian.whatsapp.modules.setting.SettingRepository;
import com.damian.whatsapp.modules.setting.dto.request.SettingUpdateRequest;
import com.damian.whatsapp.modules.setting.dto.request.SettingsPatchRequest;
import com.damian.whatsapp.modules.setting.exception.SettingNotFoundException;
import com.damian.whatsapp.modules.setting.exception.SettingNotOwnerException;
import com.damian.whatsapp.shared.domain.Setting;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

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
    public Set<Setting> getSettings() {
        User currentUser = AuthHelper.getLoggedUser();
        return settingRepository.findByUser_Id(currentUser.getId());
    }

    // update only one setting
    public Setting updateSetting(Long settingId, SettingUpdateRequest request) {
        User currentUser = AuthHelper.getLoggedUser();

        // find the setting by settingId
        Setting setting = settingRepository.findById(settingId).orElseThrow(
                () -> new SettingNotFoundException(Exceptions.SETTINGS.NOT_FOUND, settingId)
        );

        // check if the logged user is the owner of the setting.
        if (!setting.isOwner(currentUser)) {
            throw new SettingNotOwnerException(Exceptions.SETTINGS.NOT_OWNER, currentUser.getId());
        }

        setting.setSettingValue(request.value());

        log.debug(
                "Updated setting: {} with value: {} by user: {}",
                setting.getSettingKey(),
                setting.getSettingValue(),
                currentUser.getId()
        );

        return settingRepository.save(setting);
    }

    // update multiple settings at once
    public Set<Setting> updateSettings(SettingsPatchRequest request) {
        request.settings().forEach((id, value) -> {
            this.updateSetting(id, new SettingUpdateRequest(value));
        });

        return this.getSettings();
    }
}
