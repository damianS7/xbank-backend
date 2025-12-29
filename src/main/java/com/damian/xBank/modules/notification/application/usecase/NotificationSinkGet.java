package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class NotificationSinkGet {
    private static final Logger log = LoggerFactory.getLogger(NotificationSinkGet.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationSinkRegistry notificationSinkRegistry;

    public NotificationSinkGet(
            AuthenticationContext authenticationContext,
            NotificationSinkRegistry notificationSinkRegistry
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationSinkRegistry = notificationSinkRegistry;
    }

    /**
     * Get notifications for the current user as a Flux stream.
     * The stream will be closed when the client disconnects.
     *
     * @return Flux<NotificationEvent> a stream of notifications
     */
    public Flux<NotificationEvent> execute() {
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