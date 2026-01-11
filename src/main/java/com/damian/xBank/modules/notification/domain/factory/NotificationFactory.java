package com.damian.xBank.modules.notification.domain.factory;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.user.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {
    public Notification createFromNotificationEvent(User owner, NotificationEvent notificationEvent) {
        return Notification
                .create(owner)
                .setMetadata(notificationEvent.payload())
                .setType(notificationEvent.type())
                .setTemplateKey(notificationEvent.templateKey()
                );
    }
}
