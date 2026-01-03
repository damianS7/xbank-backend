package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationDelete {
    private static final Logger log = LoggerFactory.getLogger(NotificationDelete.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;

    public NotificationDelete(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Delete notification for the current user.
     *
     * @param id Notification id
     */
    @Transactional
    public void execute(Long id) {
        final User currentUser = authenticationContext.getCurrentUser();

        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotificationNotFoundException(id)
                );

        // Assert notification is owned by currentUser or throw
        notification.assertOwnedBy(currentUser.getId());

        // delete notification
        notificationRepository.delete(notification);

        log.debug("Deleted notification: {}", notification.getId());
    }
}