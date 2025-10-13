package com.damian.xBank.modules.setting.service;

import com.damian.xBank.modules.setting.dto.request.SettingsPatchRequest;
import com.damian.xBank.modules.setting.exception.SettingNotFoundException;
import com.damian.xBank.modules.setting.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.repository.SettingRepository;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.Setting;
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
        Customer currentCustomer = AuthHelper.getLoggedCustomer();
        return settingRepository.findByCustomer_Id(currentCustomer.getId())
                                .orElseThrow(
                                        () -> new SettingNotFoundException(
                                                Exceptions.CUSTOMER.SETTINGS.NOT_FOUND
                                        )
                                );
    }

    // update only one setting
    public Setting updateSetting(String key, String value) {
        Customer currentCustomer = AuthHelper.getLoggedCustomer();

        // find the setting by settingId
        Setting setting = settingRepository.findByCustomer_Id(currentCustomer.getId()).orElseThrow(
                () -> new SettingNotFoundException(Exceptions.CUSTOMER.SETTINGS.NOT_FOUND, currentCustomer.getId())
        );

        // check if the logged user is the owner of the setting.
        if (!setting.isOwner(currentCustomer)) {
            throw new SettingNotOwnerException(Exceptions.CUSTOMER.SETTINGS.NOT_OWNER, currentCustomer.getId());
        }

        //        setting.setSettings(request.value());

        log.debug(
                "Updated setting: {} with value: {} by user: {}",
                key,
                value,
                currentCustomer.getId()
        );

        setting.getSettings().put(key, value);
        return settingRepository.save(setting);
    }

    // update multiple settings at once
    public Setting updateSettings(SettingsPatchRequest request) {
        request.settings().forEach((key, value) -> {
            this.updateSetting(key, value);
        });

        return this.getSettings();
    }
}
