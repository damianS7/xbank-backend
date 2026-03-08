package com.damian.xBank.modules.notification.infrastructure.rest.mapper;

import com.damian.xBank.modules.notification.application.usecase.NotificationResult;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.rest.response.NotificationDto;
import org.springframework.data.domain.Page;

public class NotificationDtoMapper {
    public static NotificationDto toDto(Notification notification) {
        return new NotificationDto(
            notification.getId(),
            notification.getType(),
            notification.getMetadata(),
            notification.getTemplateKey(),
            notification.getCreatedAt()
        );
    }

    public static Page<NotificationDto> toPageDto(Page<Notification> notifications) {
        return notifications.map(
            NotificationDtoMapper::toDto
        );
    }

    public static NotificationResult toResult(Notification notification) {
        return new NotificationResult(
            notification.getId(),
            notification.getType(),
            notification.getMetadata(),
            notification.getTemplateKey(),
            notification.getCreatedAt()
        );
    }

    public static Page<NotificationResult> toPageResult(Page<Notification> notifications) {
        return notifications.map(
            NotificationDtoMapper::toResult
        );
    }
}
