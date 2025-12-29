package com.damian.xBank.modules.notification.domain.service;

import com.damian.xBank.modules.notification.domain.model.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationDomainService {

    public NotificationDomainService() {
    }

    // TODO review this ... maybe unnecesary, move to usecase?

    /**
     * Delete a notification.
     *
     * @param userId
     * @param notification
     */
    public void deleteNotification(Long userId, Notification notification) {

        // Assert notification is owned by currentUser or throw
        notification.assertOwnedBy(userId);
    }
}