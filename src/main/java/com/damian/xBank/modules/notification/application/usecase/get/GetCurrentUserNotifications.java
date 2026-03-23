package com.damian.xBank.modules.notification.application.usecase.get;

import com.damian.xBank.modules.notification.application.dto.NotificationResult;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Caso de uso que devuelve todas las notificaciones del usuario actual.
 */
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
     * @param query La consulta con los datos necesarios
     * @return Page<Notification> Notificaciones paginadas
     */
    public PageResult<NotificationResult> execute(GetCurrentUserNotificationsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();
        log.debug("Fetching notifications for user: {}", currentUser.getId());


        Page<Notification> notifications = notificationRepository.findAllByUserId(
            currentUser.getId(),
            query.pageable()
        );

        return PageResult.fromPagedNotifications(notifications);
    }
}