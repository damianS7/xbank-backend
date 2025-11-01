package com.damian.xBank.modules.notification.controller;

import com.damian.xBank.modules.notification.dto.NotificationEvent;
import com.damian.xBank.modules.notification.dto.mapper.NotificationDtoMapper;
import com.damian.xBank.modules.notification.dto.response.NotificationDto;
import com.damian.xBank.modules.notification.service.NotificationService;
import com.damian.xBank.shared.domain.Notification;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<Notification> notifications = notificationService.getNotifications(pageable);
        Page<NotificationDto> notificationsDto = NotificationDtoMapper.map(notifications);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationsDto);
    }

    // endpoint to delete a notifications
    @DeleteMapping("/notifications")
    public ResponseEntity<?> deleteNotifications(
    ) {
        notificationService.deleteNotifications();

        return ResponseEntity
                .noContent()
                .build();
    }

    // endpoint to delete a notifications
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