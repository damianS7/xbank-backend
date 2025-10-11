package com.damian.xBank.modules.notification.dto;

import com.damian.xBank.modules.notification.NotificationType;

import java.util.Map;

public record NotificationEvent(
        Long recipientId,
        NotificationType type,
        Map<String, Object> metadata,
        String message,
        String createdAt
) {
}