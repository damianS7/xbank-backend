package com.damian.xBank.modules.notification.infrastructure.service;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.rest.mapper.NotificationDtoMapper;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Envía (publish) notificaciones a un usuario.
 */
@Service
public class NotificationPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationSinkRegistry sinkRegistry;

    public NotificationPublisher(
        NotificationRepository notificationRepository,
        UserRepository userRepository,
        NotificationSinkRegistry sinkRegistry
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.sinkRegistry = sinkRegistry;
    }

    /**
     * @param notificationEvent El evento a publicar
     */
    public void publish(NotificationEvent notificationEvent) {
        // Busca el usuario receptor de la notificación
        User recipient = userRepository
            .findById(notificationEvent.toUserId())
            .orElseThrow(
                () -> new UserNotFoundException(notificationEvent.toUserId())
            );

        Notification notification = Notification.create(
            recipient,
            notificationEvent.type(),
            notificationEvent.payload(),
            notificationEvent.templateKey()
        );

        // Se guarda la notificación en bd para que el usuario la pueda leer incluso si pulsa f5
        notificationRepository.save(notification);

        // Se envía la notificación al usuario a traves del sink/stream
        var sink = sinkRegistry.getSinkForUser(notificationEvent.toUserId());

        if (sink != null) {
            sink.tryEmitNext(
                NotificationDtoMapper.toDto(notification)
            );
        }

        log.debug(
            "{} notification {} with {} sent to user: {}",
            notification.getType(),
            notification.getId(),
            notification.getMetadata().toString(),
            notification.getOwner().getId()
        );
    }
}