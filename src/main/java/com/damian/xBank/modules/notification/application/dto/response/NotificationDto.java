package com.damian.xBank.modules.notification.application.dto.response;

import com.damian.xBank.modules.notification.domain.enums.NotificationType;

import java.util.Map;

public record NotificationDto(
        Long id,
        NotificationType type,
        Map<String, Object> metadata,
        String createdAt
) {
}