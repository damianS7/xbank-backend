package com.damian.xBank.modules.setting.infrastructure.rest.controller;

import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettings;
import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsQuery;
import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsResult;
import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettings;
import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettingsCommand;
import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettingsResult;
import com.damian.xBank.modules.setting.infrastructure.mapper.SettingMapper;
import com.damian.xBank.modules.setting.infrastructure.rest.request.UpdateCurrentUserSettingsRequest;
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

    /**
     * Endpoint para obtener las settings
     *
     * @return GetCurrentUserSettingsResult
     */
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        GetCurrentUserSettingsQuery query = SettingMapper.toGetCurrentUserSettingsQuery();
        GetCurrentUserSettingsResult result = getCurrentUserSettings.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }

    /**
     * Endpoint para actualizar las settings
     *
     * @param request Petición con los datos a modificar
     * @return UpdateCurrentUserSettingsRequest
     */
    @PatchMapping("/settings")
    public ResponseEntity<?> updateSettings(
        @Valid @RequestBody
        UpdateCurrentUserSettingsRequest request
    ) {
        UpdateCurrentUserSettingsCommand command = SettingMapper.toCommand(request);
        UpdateCurrentUserSettingsResult result = updateCurrentUserSettings.execute(command);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(result);
    }
}

