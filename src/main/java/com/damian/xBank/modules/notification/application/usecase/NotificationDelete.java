package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.service.NotificationDomainService;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationDelete {
    private static final Logger log = LoggerFactory.getLogger(NotificationDelete.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;
    private final NotificationDomainService notificationDomainService;

    public NotificationDelete(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository,
            NotificationDomainService notificationDomainService
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
        this.notificationDomainService = notificationDomainService;
    }

    /**
     * Delete all notifications for the current user.
     *
     * @param notificationIds List of notification ids to delete
     */
    @Transactional
    public void deleteNotifications(List<Long> notificationIds) {
        final User currentUser = authenticationContext.getCurrentUser();

        // delete selected notifications
        notificationRepository.deleteAllByIdInAndUser_Id(
                notificationIds,
                currentUser.getAccount().getId()
        );

        log.debug("Deleted {} notifications from user: {}", notificationIds.size(), currentUser.getId());
    }

    /**
     * Delete notification for the current user.
     *
     * @param id Notification id
     */
    @Transactional
    public void deleteNotification(Long id) {
        final User currentUser = authenticationContext.getCurrentUser();

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotificationNotFoundException(id)
                );

        // Assert notification is owned by currentUser or throw
        notificationDomainService.deleteNotification(currentUser.getId(), notification);

        // delete notification
        notificationRepository.delete(notification);

        log.debug("Deleted notification: {}", notification.getId());
    }
}