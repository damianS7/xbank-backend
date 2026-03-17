package com.damian.xBank.modules.notification.application.usecase.get;

import com.damian.xBank.modules.notification.application.dto.NotificationResult;
import com.damian.xBank.modules.notification.infrastructure.sink.NotificationSinkRegistry;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Caso de uso para obtener las notificaciones en tiempo real.
 */
@Service
public class GetCurrentUserSinkNotifications {
    private static final Logger log = LoggerFactory.getLogger(GetCurrentUserSinkNotifications.class);
    private final AuthenticationContext authenticationContext;
    private final NotificationSinkRegistry notificationSinkRegistry;

    public GetCurrentUserSinkNotifications(
        AuthenticationContext authenticationContext,
        NotificationSinkRegistry notificationSinkRegistry
    ) {
        this.authenticationContext = authenticationContext;
        this.notificationSinkRegistry = notificationSinkRegistry;
    }

    /**
     * @return Flux<NotificationEvent> Un stream de notificaciones
     */
    public Flux<NotificationResult> execute() {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Crea o recupera (si existe) el sink del usuario actual
        Sinks.Many<NotificationResult> sink =
            notificationSinkRegistry.getSinkForUserOrCreate(currentUser.getId());

        // Devuelve un stream con las notificaciones
        return sink.asFlux()
            .doOnSubscribe(subscription ->
                log.debug("User {} subscribed to SSE stream", currentUser.getId()))
            .doOnNext(event ->
                log.debug(
                    "Sending SSE event: {} to user {}",
                    event.toString(), currentUser.getId()
                ))
            .doOnError(error ->
                log.error(
                    "SSE error for user {}: {}",
                    currentUser.getId(), error.getMessage()
                ))
            .doFinally(signalType -> {
                    log.debug(
                        "SSE stream ended for user {}: {}",
                        currentUser.getId(), signalType
                    );
                }
            );
    }
}