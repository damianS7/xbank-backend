package com.damian.xBank.test.utils;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.user.user.domain.model.User;

import java.util.HashMap;
import java.util.Map;

public class NotificationTestBuilder {
    private Long id = null;
    private User owner;
    private NotificationType type = NotificationType.TRANSFER;
    private String templateKey = "templateKey";
    private Map<String, Object> payload = new HashMap<>();

    public static NotificationTestBuilder builder() {
        return new NotificationTestBuilder();
    }

    public NotificationTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public NotificationTestBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public NotificationTestBuilder withType(NotificationType type) {
        this.type = type;
        return this;
    }

    public NotificationTestBuilder withTemplateKey(String templateKey) {
        this.templateKey = templateKey;
        return this;
    }

    public NotificationTestBuilder withPayload(Map<String, Object> payload) {
        this.payload = payload;
        return this;
    }

    public Notification build() {
        return Notification.reconstitute(id, owner, type, payload, templateKey, null);
    }
}