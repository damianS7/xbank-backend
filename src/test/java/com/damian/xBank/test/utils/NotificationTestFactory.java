package com.damian.xBank.test.utils;

import com.damian.xBank.modules.notification.domain.model.NotificationType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationTestFactory {

    public static NotificationTestBuilder aNotification() {
        return NotificationTestBuilder.builder()
            .withType(NotificationType.TRANSFER)
            .withPayload(Map.of(
                "transactionId", 1L,
                "toUser", 1L,
                "amount", 100L,
                "currency", "EUR"
            ))
            .withTemplateKey("testTemplateKey");
    }

    public static NotificationTestBuilder aNotificationWithId(Long id) {
        return NotificationTestBuilder.builder()
            .withId(id)
            .withType(NotificationType.TRANSFER)
            .withPayload(Map.of(
                "transactionId", 1L,
                "toUser", 1L,
                "amount", 100L,
                "currency", "EUR"
            ))
            .withTemplateKey("testTemplateKey");
    }

}