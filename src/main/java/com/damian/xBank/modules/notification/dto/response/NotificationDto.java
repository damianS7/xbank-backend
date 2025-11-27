package com.damian.xBank.modules.notification.dto.response;

import com.damian.xBank.modules.notification.enums.NotificationType;

import java.util.Map;

public record NotificationDto(
        Long id,
        NotificationType type,
        Map<String, Object> metadata,
        String createdAt
) {
}