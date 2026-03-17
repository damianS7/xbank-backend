package com.damian.xBank.modules.notification.application.usecase.delete;

import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para borrar varias notificaciones pertenecientes al usuario actual.
 */
@Service
public class DeleteNotifications {
    private static final Logger log = LoggerFactory.getLogger(DeleteNotifications.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;

    public DeleteNotifications(
        AuthenticationContext authenticationContext,
        NotificationRepository notificationRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
    }

    /**
     * @param command Comando con un List de ids para borrar
     */
    @Transactional
    public void execute(DeleteNotificationsCommand command) {
        final User currentUser = authenticationContext.getCurrentUser();

        // Borrar notificaciones
        notificationRepository.deleteAllByIdInAndUser_Id(
            command.notificationIds(),
            currentUser.getId()
        );

        log.debug(
            "Deleted {} notifications with ids: {} from user: {}",
            command.notificationIds().size(),
            command.notificationIds(),
            currentUser.getId()
        );
    }
}