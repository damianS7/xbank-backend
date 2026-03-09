package com.damian.xBank.modules.notification.infrastructure.sink;

import com.damian.xBank.modules.notification.application.dto.NotificationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationSinkRegistry {
    private final Map<Long, Sinks.Many<NotificationResult>> userSinks = new ConcurrentHashMap<>();

    public NotificationSinkRegistry() {
    }

    public Map<Long, Sinks.Many<NotificationResult>> getUserSinks() {
        return userSinks;
    }

    public Sinks.Many<NotificationResult> getSinkForUser(Long userId) {
        return userSinks.get(userId);
    }

    public Sinks.Many<NotificationResult> getSinkForUserOrCreate(Long userId) {
        return userSinks.computeIfAbsent(
            userId,
            k -> Sinks.many().multicast().onBackpressureBuffer()
        );

    }

    public void removeSink(Long userId) {
        userSinks.remove(userId);
    }
}