package com.damian.xBank.modules.notification.application.usecase;

import com.damian.xBank.modules.notification.application.dto.response.NotificationDto;
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
     *
     * @return Flux<NotificationEvent> a stream of notifications
     */
    public Flux<ServerSentEvent<?>> execute() {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        Sinks.Many<NotificationDto> sink =
                notificationSinkRegistry.getSinkForUserOrCreate(currentUser.getId());

        Flux<ServerSentEvent<NotificationDto>> notifications =
                sink.asFlux()
                    .map(dto ->
                            ServerSentEvent.builder(dto)
                                           .event("notification")
                                           .data(dto)
                                           .build()
                    );

        Flux<ServerSentEvent<?>> heartbeat = Flux
                .interval(Duration.ofSeconds(10))
                .map(tick -> ServerSentEvent.builder()
                                            .event("heartbeat")
                                            .comment("ping")
                                            .data("ping")
                                            .build()
                );

        return Flux.merge(notifications, heartbeat)
                   .doOnSubscribe(subscription ->
                           log.debug("✅ User {} subscribed to SSE stream", currentUser.getId()))
                   .doOnNext(event ->
                           log.debug(
                                   "📤 Sending SSE event: {} to user {}",
                                   event.data(), currentUser.getId()
                           ))
                   .doOnError(error ->
                           log.error(
                                   "❌ SSE error for user {}: {}",
                                   currentUser.getId(), error.getMessage()
                           ))
                   .doFinally(signalType -> {
                               log.debug(
                                       "🔚 SSE stream ended for user {}: {}",
                                       currentUser.getId(), signalType
                               );
                               //                               notificationSinkRegistry.removeSink(currentUser.getId());
                           }
                   );
    }
}