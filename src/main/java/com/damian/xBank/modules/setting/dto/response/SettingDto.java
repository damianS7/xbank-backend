package com.damian.xBank.modules.setting.dto.response;

import java.util.Map;

public record SettingDto(
        Long id,
        Map<String, Object> settings
) {
}
