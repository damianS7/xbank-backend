package com.damian.xBank.shared.infrastructure.messaging;

import com.damian.xBank.config.RabbitConfig;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Consume notificaciones de una cola de RabbitMQ.
 */
@Service
public class NotificationConsumer {
    private final NotificationPublisher notificationPublisher;

    public NotificationConsumer(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * Escucha una cola de RabbitMQ y procesa las notificaciones.
     *
     * @param notification El evento recibido de la cola.
     */
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receiveMessage(NotificationEvent notification) {
        notificationPublisher.publish(notification);
    }
}