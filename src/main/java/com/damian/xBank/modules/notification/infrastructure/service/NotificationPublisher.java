package com.damian.xBank.modules.notification.infrastructure.service;

import com.damian.xBank.modules.notification.application.mapper.NotificationDtoMapper;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
     * Publish a notification event to the recipient.
     *
     * @param notificationEvent the notification event
     */
    public void publish(NotificationEvent notificationEvent) {
        // find recipient user who will receive the notification
        User recipient = userRepository
                .findById(notificationEvent.toUserId())
                .orElseThrow(() -> {
                    log.warn(
                            "Notification failed: recipient: {} not found.",
                            notificationEvent.toUserId()
                    );
                    return new UserNotFoundException(notificationEvent.toUserId());
                });

        // create and save notification to the database
        Notification notification = Notification
                .create(recipient)
                .setMetadata(notificationEvent.payload())
                .setType(notificationEvent.type())
                .setTemplateKey(notificationEvent.templateKey());

        notificationRepository.save(notification);

        // emit event to the recipient if connected
        var sink = sinkRegistry.getSinkForUser(notificationEvent.toUserId());

        if (sink != null) {
            sink.tryEmitNext(
                    NotificationDtoMapper.map(notification)
            );
        }

        log.debug(
                "Notification ({}) sent to user: {}",
                notificationEvent.type(),
                notificationEvent.toUserId()
        );
    }
}