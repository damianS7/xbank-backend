package com.damian.xBank.modules.setting.controller;

import com.damian.xBank.modules.setting.dto.mapper.SettingDtoMapper;
import com.damian.xBank.modules.setting.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.dto.response.SettingDto;
import com.damian.xBank.modules.setting.service.SettingService;
import com.damian.xBank.shared.domain.Setting;
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

