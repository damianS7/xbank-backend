package com.damian.xBank.modules.notification.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class NotificationNotOwnerException extends NotificationException {
    public NotificationNotOwnerException(Long notificationId, Long userId) {
        super(ErrorCodes.NOTIFICATION_NOT_OWNER, notificationId, new Object[]{userId});
    }
}
