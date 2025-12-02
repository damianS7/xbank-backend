package com.damian.xBank.modules.setting.dto.mapper;

import com.damian.xBank.modules.setting.dto.response.SettingDto;
import com.damian.xBank.modules.setting.model.Setting;

import java.util.Set;
import java.util.stream.Collectors;

public class SettingDtoMapper {
    public static SettingDto toSettingDto(Setting setting) {
        return new SettingDto(
                setting.getSettings()
        );
    }

    public static Set<SettingDto> toSettingDtoList(Set<Setting> settings) {
        return settings
                .stream()
                .map(
                        SettingDtoMapper::toSettingDto
                ).collect(Collectors.toSet());
    }
}
