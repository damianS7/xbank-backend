package com.damian.xBank.modules.notification.infrastructure.rest.controller;

import com.damian.xBank.modules.notification.application.dto.NotificationResult;
import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotification;
import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotificationCommand;
import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotifications;
import com.damian.xBank.modules.notification.application.usecase.delete.DeleteNotificationsCommand;
import com.damian.xBank.modules.notification.application.usecase.get.GetCurrentUserNotifications;
import com.damian.xBank.modules.notification.application.usecase.get.GetCurrentUserNotificationsQuery;
import com.damian.xBank.modules.notification.application.usecase.get.GetCurrentUserNotificationsResult;
import com.damian.xBank.modules.notification.application.usecase.get.GetCurrentUserSinkNotifications;
import com.damian.xBank.modules.notification.infrastructure.rest.request.NotificationDeleteRequest;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Validated
@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private final DeleteNotification deleteNotification;
    private final DeleteNotifications deleteNotifications;
    private final GetCurrentUserNotifications getCurrentUserNotifications;
    private final GetCurrentUserSinkNotifications getCurrentUserSinkNotifications;
    private final NotificationPublisher notificationPublisher;

    public NotificationController(
        DeleteNotification deleteNotification,
        DeleteNotifications deleteNotifications,
        GetCurrentUserNotifications getCurrentUserNotifications,
        GetCurrentUserSinkNotifications getCurrentUserSinkNotifications,
        NotificationPublisher notificationPublisher
    ) {
        this.deleteNotification = deleteNotification;
        this.deleteNotifications = deleteNotifications;
        this.getCurrentUserNotifications = getCurrentUserNotifications;
        this.getCurrentUserSinkNotifications = getCurrentUserSinkNotifications;
        this.notificationPublisher = notificationPublisher;
    }

    // endpoint to fetch (paginated) notifications from current user
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
        @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        GetCurrentUserNotificationsQuery query = new GetCurrentUserNotificationsQuery(pageable);
        GetCurrentUserNotificationsResult notifications = getCurrentUserNotifications.execute(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(notifications.pagedResult());
    }

    // endpoint to delete a batch of notifications by its id
    @DeleteMapping("/notifications")
    public ResponseEntity<?> deleteNotifications(
        @Valid @RequestBody
        NotificationDeleteRequest request
    ) {
        DeleteNotificationsCommand command = new DeleteNotificationsCommand(request.notificationIds());
        deleteNotifications.execute(command);

        return ResponseEntity
            .noContent()
            .build();
    }

    // endpoint to delete a notification by id
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<?> deleteNotification(
        @PathVariable @Positive
        Long id
    ) {
        DeleteNotificationCommand command = new DeleteNotificationCommand(id);
        deleteNotification.execute(command);

        return ResponseEntity
            .noContent()
            .build();
    }

    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamNotifications() {
        Flux<ServerSentEvent<NotificationResult>> notifications =
            getCurrentUserSinkNotifications.execute()
                .map(dto -> ServerSentEvent.builder(dto)
                    .event("notification")
                    .build()
                );

        Flux<ServerSentEvent<String>> heartbeat =
            Flux.interval(Duration.ofSeconds(10))
                .map(tick -> ServerSentEvent.<String>builder()
                    .event("heartbeat")
                    .data("ping")
                    .build()
                );

        return Flux.merge(notifications, heartbeat);
    }
}