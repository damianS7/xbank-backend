package com.damian.xBank.modules.notification.infrastructure.sink;

import com.damian.xBank.modules.notification.application.dto.response.NotificationDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationSinkRegistry {
    private final Map<Long, Sinks.Many<NotificationDto>> userSinks = new ConcurrentHashMap<>();

    public NotificationSinkRegistry() {
    }

    public Map<Long, Sinks.Many<NotificationDto>> getUserSinks() {
        return userSinks;
    }

    public Sinks.Many<NotificationDto> getSinkForUser(Long userId) {
        return userSinks.get(userId);
    }

    public Sinks.Many<NotificationDto> getSinkForUserOrCreate(Long userId) {
        return userSinks.computeIfAbsent(
                userId,
                k -> Sinks.many().multicast().onBackpressureBuffer()
        );

    }

    public void removeSink(Long userId) {
        userSinks.remove(userId);
    }
}