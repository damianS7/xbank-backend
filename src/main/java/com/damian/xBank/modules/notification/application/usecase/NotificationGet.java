package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationGet {
    private static final Logger log = LoggerFactory.getLogger(NotificationGet.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;

    public NotificationGet(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Get notifications for the current user.
     *
     * @param pageable pagination params
     * @return Page<Notification> a page of notifications
     */
    public Page<Notification> execute(Pageable pageable) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        log.debug("Fetching notifications for user: {}", currentUser.getId());

        // Fetch and return notifications for the current user
        return notificationRepository.findAllByUserId(currentUser.getId(), pageable);
    }
}