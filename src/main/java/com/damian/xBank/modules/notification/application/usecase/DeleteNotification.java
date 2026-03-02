package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.application.cqrs.command.DeleteNotificationCommand;
import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteNotification {
    private static final Logger log = LoggerFactory.getLogger(DeleteNotification.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;

    public DeleteNotification(
        AuthenticationContext authenticationContext,
        NotificationRepository notificationRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Delete notification for the current user.
     *
     * @param command with the Notification id
     */
    @Transactional
    public void execute(DeleteNotificationCommand command) {
        final User currentUser = authenticationContext.getCurrentUser();

        Notification notification = notificationRepository
            .findById(command.id())
            .orElseThrow(
                () -> new NotificationNotFoundException(command.id())
            );

        // Assert notification is owned by currentUser or throw
        notification.assertOwnedBy(currentUser.getId());

        // delete notification
        notificationRepository.delete(notification);

        log.debug("Deleted notification: {}", notification.getId());
    }
}