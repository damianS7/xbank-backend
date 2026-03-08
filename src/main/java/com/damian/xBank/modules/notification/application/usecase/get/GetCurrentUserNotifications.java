package com.damian.xBank.modules.notification.application.usecase.get;

import com.damian.xBank.modules.notification.application.usecase.NotificationResult;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.rest.mapper.NotificationDtoMapper;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserNotifications {
    private static final Logger log = LoggerFactory.getLogger(GetCurrentUserNotifications.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;

    public GetCurrentUserNotifications(
        AuthenticationContext authenticationContext,
        NotificationRepository notificationRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Get notifications for the current user.
     *
     * @param query pagination params
     * @return Page<Notification> a page of notifications
     */
    public GetCurrentUserNotificationsResult execute(GetCurrentUserNotificationsQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        log.debug("Fetching notifications for user: {}", currentUser.getId());

        Page<Notification> notifications = notificationRepository.findAllByUserId(
            currentUser.getId(),
            query.pageable()
        );
        Page<NotificationResult> notificationsResult = NotificationDtoMapper.toPageResult(notifications);

        // Fetch and return notifications for the current user
        return new GetCurrentUserNotificationsResult(notificationsResult);
    }
}