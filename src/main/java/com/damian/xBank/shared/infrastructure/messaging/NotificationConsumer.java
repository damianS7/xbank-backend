package com.damian.xBank.shared.infrastructure.messaging;

import com.damian.xBank.config.RabbitConfig;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Responsible for consuming notification messages from a RabbitMQ queue.
 */
@Service
public class NotificationConsumer {
    private final NotificationPublisher notificationPublisher;

    public NotificationConsumer(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    /**
     * Listens for messages from the RabbitMQ queue and processes them.
     *
     * @param notification The notification event received from the queue.
     */
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receiveMessage(NotificationEvent notification) {
        // Delegates the processing of the notification to the NotificationService
        notificationPublisher.publish(notification);
    }
}