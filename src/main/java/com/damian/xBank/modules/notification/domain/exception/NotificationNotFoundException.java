package com.damian.xBank.modules.notification.domain.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(Long notificationId) {
        super(Exceptions.COMMON.NOT_FOUND, notificationId, 0L);
    }
}
