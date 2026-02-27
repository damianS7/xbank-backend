package com.damian.xBank.modules.setting.infrastructure.rest.controller;

import com.damian.xBank.modules.setting.application.cqrs.command.SettingUpdateCommand;
import com.damian.xBank.modules.setting.application.cqrs.result.SettingResult;
import com.damian.xBank.modules.setting.application.usecase.SettingGet;
import com.damian.xBank.modules.setting.application.usecase.SettingUpdate;
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
    public ResponseEntity<SettingResult> getSettings() {
        SettingResult settingResult = settingGet.execute();

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
        SettingUpdateCommand command = SettingDtoMapper.toCommand(request);
        SettingResult settingResult = settingUpdate.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(settingResult);
    }
}

