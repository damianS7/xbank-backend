package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

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
    public Flux<NotificationEvent> execute2() {
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

    public Flux<ServerSentEvent<NotificationEvent>> execute() {

        User currentUser = authenticationContext.getCurrentUser();

        Sinks.Many<NotificationEvent> sink =
                notificationSinkRegistry.getSinkForUserOrCreate(currentUser.getId());

        Flux<ServerSentEvent<NotificationEvent>> notifications =
                sink.asFlux()
                    .map(event ->
                            ServerSentEvent.builder(event)
                                           .event("notification")
                                           .id(event.toUserId().toString())
                                           .build()
                    );

        Flux<ServerSentEvent<NotificationEvent>> heartbeat =
                Flux.interval(Duration.ofSeconds(25))
                    .map(i -> ServerSentEvent.<NotificationEvent>builder()
                                             .comment("ping")
                                             .build()
                    );

        return Flux.merge(notifications, heartbeat)
                   .doOnCancel(() -> notificationSinkRegistry.removeSink(currentUser.getId()));
    }
}