package com.damian.xBank.modules.notification.dto.response;

import com.damian.xBank.modules.notification.NotificationType;

import java.util.Map;

public record NotificationDto(
        Long id,
        NotificationType type,
        String message,
        Map<String, Object> metadata,
        String createdAt
) {
}