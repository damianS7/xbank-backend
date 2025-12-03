package com.damian.xBank.modules.notification.infra.controller;

import com.damian.xBank.modules.notification.application.dto.mapper.NotificationDtoMapper;
import com.damian.xBank.modules.notification.application.dto.request.NotificationDeleteRequest;
import com.damian.xBank.modules.notification.application.dto.response.NotificationDto;
import com.damian.xBank.modules.notification.domain.event.NotificationEvent;
import com.damian.xBank.modules.notification.domain.entity.Notification;
import com.damian.xBank.modules.notification.application.service.NotificationService;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // endpoint to fetch (paginated) notifications from current user
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Notification> notifications = notificationService.getNotifications(pageable);
        Page<NotificationDto> notificationsDto = NotificationDtoMapper.map(notifications);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationsDto);
    }

    // endpoint to delete a batch of notifications by its id
    @DeleteMapping("/notifications")
    public ResponseEntity<?> deleteNotifications(
            @Validated @RequestBody
            NotificationDeleteRequest request
    ) {
        notificationService.deleteNotifications(
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
        notificationService.deleteNotification(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationEvent> streamNotifications() {
        return notificationService.getNotificationsForUser();
    }
}