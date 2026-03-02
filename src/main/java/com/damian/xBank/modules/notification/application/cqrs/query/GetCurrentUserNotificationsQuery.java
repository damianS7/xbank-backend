package com.damian.xBank.modules.notification.application.cqrs.query;

import org.springframework.data.domain.Pageable;

public record GetCurrentUserNotificationsQuery(
    Pageable pageable
) {
}
