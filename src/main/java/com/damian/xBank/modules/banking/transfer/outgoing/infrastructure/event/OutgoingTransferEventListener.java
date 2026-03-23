package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.event;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.event.OutgoingTransferCompletedEvent;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.event.OutgoingTransferSentEvent;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Map;

@Component
public class OutgoingTransferEventListener {
    private final NotificationPublisher notificationPublisher;

    public OutgoingTransferEventListener(
        NotificationPublisher notificationPublisher
    ) {
        this.notificationPublisher = notificationPublisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(OutgoingTransferCompletedEvent event) {
        // 1. Notify sender
        NotificationEvent senderEvent = new NotificationEvent(
            event.senderUserId(),
            NotificationType.TRANSACTION,
            Map.of(
                "transactionId", event.senderTransactionId(),
                "toUser", event.senderUserName(),
                "amount", event.amount(),
                "currency", event.currency()
            ),
            "notification.transfer.sent",
            Instant.now()
        );
        notificationPublisher.publish(senderEvent);

        // 2. Notify recipient (logic moved here)
        if (event.transferType() == OutgoingTransferType.INTERNAL) {
            NotificationEvent recipientEvent = new NotificationEvent(
                event.recipientUserId(),
                NotificationType.TRANSACTION,
                Map.of(
                    "transactionId", event.recipientTransactionId(),
                    "fromUser", event.recipientUserName(),
                    "amount", event.amount(),
                    "currency", event.currency()
                ),
                "notification.transfer.received",
                Instant.now()
            );

            notificationPublisher.publish(recipientEvent);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferSent(OutgoingTransferSentEvent event) {
        NotificationEvent notificationEvent = new NotificationEvent(
            event.toUserId(),
            NotificationType.TRANSACTION,
            Map.of(
                "transactionId", event.transactionId(),
                "toUser", event.toUser(),
                "amount", event.amount(),
                "currency", event.currency()
            ),
            "notification.transfer.sent",
            Instant.now()
        );
        notificationPublisher.publish(notificationEvent);
    }
}