package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.repository.NotificationRepository;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationGet {
    private static final Logger log = LoggerFactory.getLogger(NotificationGet.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationRepository notificationRepository;
    private final NotificationSinkRegistry notificationSinkRegistry;

    public NotificationGet(
            AuthenticationContext authenticationContext,
            NotificationRepository notificationRepository,
            NotificationSinkRegistry notificationSinkRegistry
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationRepository = notificationRepository;
        this.notificationSinkRegistry = notificationSinkRegistry;
    }

    /**
     * Get notifications for the current user.
     *
     * @param pageable pagination params
     * @return Page<Notification> a page of notifications
     */
    public Page<Notification> getNotifications(Pageable pageable) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        log.debug("Fetching notifications for user: {}", currentUser.getId());

        // Fetch and return notifications for the current user
        return notificationRepository.findAllByUserId(currentUser.getId(), pageable);
    }

    /**
     * Get notifications for the current user as a Flux stream.
     * The stream will be closed when the client disconnects.
     *
     * @return Flux<NotificationEvent> a stream of notifications
     */
    public Flux<NotificationEvent> getNotificationsForUser() {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // create a sink for the user if not exists
        Sinks.Many<NotificationEvent> sink = notificationSinkRegistry
                .getSinkForUserOrCreate(currentUser.getId());

        // remove when disconnect
        return sink.asFlux().doOnCancel(() -> {
            notificationSinkRegistry.removeSink(currentUser.getId());
        });
    }

}