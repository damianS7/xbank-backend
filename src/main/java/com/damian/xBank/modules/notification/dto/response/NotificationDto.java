package com.damian.whatsapp.modules.notification.dto.response;

import com.damian.whatsapp.modules.notification.NotificationType;

import java.util.Map;

public record NotificationDto(
        Long id,
        NotificationType type,
        String message,
        Map<String, Object> metadata,
        String createdAt
) {
}