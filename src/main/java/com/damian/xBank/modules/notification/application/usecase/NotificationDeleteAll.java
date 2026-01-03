package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationDeleteAll {
    private static final Logger log = LoggerFactory.getLogger(NotificationDeleteAll.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;

    public NotificationDeleteAll(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Delete all notifications for the current user.
     *
     * @param notificationIds List of notification ids to delete
     */
    @Transactional
    public void execute(List<Long> notificationIds) {
        final User currentUser = authenticationContext.getCurrentUser();

        // delete selected notifications
        notificationRepository.deleteAllByIdInAndUser_Id(
                notificationIds,
                currentUser.getId()
        );

        log.debug("Deleted {} notifications from user: {}", notificationIds.size(), currentUser.getId());
    }
}