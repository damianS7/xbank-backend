package com.damian.xBank.modules.setting.infrastructure.web.controller;

import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.application.dto.response.SettingDto;
import com.damian.xBank.modules.setting.application.mapper.SettingDtoMapper;
import com.damian.xBank.modules.setting.application.usecase.SettingGet;
import com.damian.xBank.modules.setting.application.usecase.SettingUpdate;
import com.damian.xBank.modules.setting.domain.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class SettingController {
    private final SettingGet settingGet;
    private final SettingUpdate settingUpdate;

    @Autowired
    public SettingController(
            SettingGet settingGet,
            SettingUpdate settingUpdate
    ) {
        this.settingGet = settingGet;
        this.settingUpdate = settingUpdate;
    }

    // endpoint to fetch all setting from logged user
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        Setting settings = settingGet.execute();
        SettingDto settingsDto = SettingDtoMapper.toSettingDto(settings);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(settingsDto);
    }

    // endpoint to update a setting
    @PatchMapping("/settings")
    public ResponseEntity<?> updateSettings(
            @Validated @RequestBody
            SettingsUpdateRequest request
    ) {
        Setting setting = settingUpdate.execute(request);
        SettingDto settingDto = SettingDtoMapper.toSettingDto(setting);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(settingDto);
    }
}

