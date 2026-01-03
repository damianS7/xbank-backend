package com.damian.xBank.modules.notification.infrastructure.service;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.infrastructure.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationPublisher.class);
    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationSinkRegistry sinkRegistry;

    public NotificationPublisher(
            NotificationRepository notificationRepository,
            UserAccountRepository userAccountRepository,
            NotificationSinkRegistry sinkRegistry
    ) {
        this.notificationRepository = notificationRepository;
        this.userAccountRepository = userAccountRepository;
        this.sinkRegistry = sinkRegistry;
    }

    /**
     * Publish a notification event to the recipient.
     *
     * @param notificationEvent the notification event
     */
    public void publish(NotificationEvent notificationEvent) {
        // find recipient user who will receive the notification
        User recipient = userAccountRepository
                .findById(notificationEvent.toUserId())
                .orElseThrow(() -> {
                    log.warn(
                            "Notification failed: recipient: {} not found.",
                            notificationEvent.toUserId()
                    );
                    return new UserAccountNotFoundException(notificationEvent.toUserId());
                });

        // create and save notification to the database
        Notification notification = Notification
                .create(recipient)
                .setMetadata(notificationEvent.metadata())
                .setType(notificationEvent.type());
        notificationRepository.save(notification);

        // emit event to the recipient if connected
        var sink = sinkRegistry.getSinkForUser(notificationEvent.toUserId());

        if (sink != null) {
            sink.tryEmitNext(notificationEvent);
        }

        log.debug(
                "Notification ({}) sent to user: {}",
                notificationEvent.type(),
                notificationEvent.toUserId()
        );
    }
}