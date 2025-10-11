package com.damian.xBank.modules.notification.dto.mapper;

import com.damian.xBank.modules.notification.dto.response.NotificationDto;
import com.damian.xBank.shared.domain.Notification;
import org.springframework.data.domain.Page;

public class NotificationDtoMapper {
    public static NotificationDto map(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getMetadata(),
                notification.getCreatedAt().toString()
        );
    }

    public static Page<NotificationDto> map(Page<Notification> notifications) {
        return notifications
                .map(
                        NotificationDtoMapper::map
                );
    }

}
