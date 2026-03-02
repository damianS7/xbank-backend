package com.damian.xBank.modules.setting.infrastructure.rest.controller;

import com.damian.xBank.modules.setting.application.cqrs.command.UpdateCurrentUserSettingsCommand;
import com.damian.xBank.modules.setting.application.cqrs.query.GetCurrentUserSettingsQuery;
import com.damian.xBank.modules.setting.application.cqrs.result.SettingResult;
import com.damian.xBank.modules.setting.application.usecase.GetCurrentUserSettings;
import com.damian.xBank.modules.setting.application.usecase.UpdateCurrentUserSettings;
import com.damian.xBank.modules.setting.infrastructure.mapper.SettingDtoMapper;
import com.damian.xBank.modules.setting.infrastructure.rest.dto.request.SettingsUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/v1")
@RestController
public class SettingController {
    private final GetCurrentUserSettings getCurrentUserSettings;
    private final UpdateCurrentUserSettings updateCurrentUserSettings;

    @Autowired
    public SettingController(
        GetCurrentUserSettings getCurrentUserSettings,
        UpdateCurrentUserSettings updateCurrentUserSettings
    ) {
        this.getCurrentUserSettings = getCurrentUserSettings;
        this.updateCurrentUserSettings = updateCurrentUserSettings;
    }

    // endpoint to fetch all setting from logged user
    @GetMapping("/settings")
    public ResponseEntity<SettingResult> getSettings() {
        GetCurrentUserSettingsQuery query = new GetCurrentUserSettingsQuery();
        SettingResult settingResult = getCurrentUserSettings.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(settingResult);
    }

    // endpoint to update a setting
    @PatchMapping("/settings")
    public ResponseEntity<SettingResult> updateSettings(
        @Valid @RequestBody
        SettingsUpdateRequest request
    ) {
        UpdateCurrentUserSettingsCommand command = SettingDtoMapper.toCommand(request);
        SettingResult settingResult = updateCurrentUserSettings.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(settingResult);
    }
}

