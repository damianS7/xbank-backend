package com.damian.xBank.modules.notification.application.usecase.get;

import org.springframework.data.domain.Pageable;

public record GetCurrentUserNotificationsQuery(
    Pageable pageable
) {
}
