package com.damian.xBank.modules.notification.application.dto.mapper;

import com.damian.xBank.modules.notification.application.dto.response.NotificationDto;
import com.damian.xBank.modules.notification.domain.entity.Notification;
import org.springframework.data.domain.Page;

public class NotificationDtoMapper {
    public static NotificationDto map(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getType(),
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
