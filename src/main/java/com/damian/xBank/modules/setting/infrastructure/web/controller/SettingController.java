package com.damian.xBank.modules.setting.infrastructure.web.controller;

import com.damian.xBank.modules.setting.application.dto.mapper.SettingDtoMapper;
import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.application.dto.response.SettingDto;
import com.damian.xBank.modules.setting.application.service.SettingService;
import com.damian.xBank.modules.setting.domain.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class SettingController {
    private final SettingService settingService;

    @Autowired
    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    // endpoint to fetch all setting from logged user
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        Setting settings = settingService.getSettings();
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
        Setting setting = settingService.updateSettings(request);
        SettingDto settingDto = SettingDtoMapper.toSettingDto(setting);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(settingDto);
    }
}

