package com.damian.xBank.modules.notification.infrastructure.web.controller;

import com.damian.xBank.modules.notification.application.dto.request.NotificationDeleteRequest;
import com.damian.xBank.modules.notification.application.dto.response.NotificationDto;
import com.damian.xBank.modules.notification.application.mapper.NotificationDtoMapper;
import com.damian.xBank.modules.notification.application.usecase.NotificationDelete;
import com.damian.xBank.modules.notification.application.usecase.NotificationDeleteAll;
import com.damian.xBank.modules.notification.application.usecase.NotificationGet;
import com.damian.xBank.modules.notification.application.usecase.NotificationSinkGet;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Validated
@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private final NotificationDelete notificationDelete;
    private final NotificationDeleteAll notificationDeleteAll;
    private final NotificationGet notificationGet;
    private final NotificationSinkGet notificationSinkGet;
    private final NotificationPublisher notificationPublisher;

    public NotificationController(
            NotificationDelete notificationDelete,
            NotificationDeleteAll notificationDeleteAll,
            NotificationGet notificationGet,
            NotificationSinkGet notificationSinkGet, NotificationPublisher notificationPublisher
    ) {
        this.notificationDelete = notificationDelete;
        this.notificationDeleteAll = notificationDeleteAll;
        this.notificationGet = notificationGet;
        this.notificationSinkGet = notificationSinkGet;
        this.notificationPublisher = notificationPublisher;
    }

    // endpoint to fetch (paginated) notifications from current user
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Notification> notifications = notificationGet.execute(pageable);
        Page<NotificationDto> notificationsDto = NotificationDtoMapper.map(notifications);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationsDto);
    }

    // endpoint to delete a batch of notifications by its id
    @DeleteMapping("/notifications")
    public ResponseEntity<?> deleteNotifications(
            @Valid @RequestBody
            NotificationDeleteRequest request
    ) {
        notificationDeleteAll.execute(
                request.notificationIds()
        );

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
        notificationDelete.execute(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamNotifications() {
        Flux<ServerSentEvent<NotificationDto>> notifications =
                notificationSinkGet.execute()
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