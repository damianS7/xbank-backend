package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.NotificationType;

import java.time.Instant;
import java.util.Map;

public record NotificationResult(
    Long id,
    NotificationType type,
    Map<String, Object> payload,
    String templateKey,
    Instant createdAt
) {
}