package com.damian.xBank.modules.banking.card.infrastructure.event;

import com.damian.xBank.modules.banking.card.domain.event.ATMWithdrawalEvent;
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
public class BankingCardEventListener {
    private final NotificationPublisher notificationPublisher;

    public BankingCardEventListener(
        NotificationPublisher notificationPublisher
    ) {
        this.notificationPublisher = notificationPublisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onATMWithdrawal(ATMWithdrawalEvent event) {
        notificationPublisher.publish(
            new NotificationEvent(
                event.cardId(),
                NotificationType.TRANSACTION,
                Map.of(
                    "transactionId", event.transactionId(),
                    "toUser", event.toUser(),
                    "amount", event.amount(),
                    "currency", event.currency()
                ),
                "notification.card.withdraw.completed",
                Instant.now()
            )
        );
    }
}