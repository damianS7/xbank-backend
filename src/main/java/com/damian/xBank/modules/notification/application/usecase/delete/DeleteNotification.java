package com.damian.xBank.modules.notification.application.usecase.delete;

import com.damian.xBank.modules.notification.domain.exception.NotificationNotFoundException;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para borrar una notificación perteneciente al usuario actual.
 */
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
     * @param command Comando con los datos necesarios.
     */
    @Transactional
    public void execute(DeleteNotificationCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        Notification notification = notificationRepository
            .findById(command.id())
            .orElseThrow(
                () -> new NotificationNotFoundException(command.id())
            );

        // Comprobar que la notificación es del usuario actual
        notification.assertOwnedBy(currentUser.getId());

        // Borra la notificación
        notificationRepository.delete(notification);

        log.debug("Deleted notification: {}", command.id());
    }
}