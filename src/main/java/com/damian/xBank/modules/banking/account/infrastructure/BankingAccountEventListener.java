package com.damian.xBank.modules.banking.account.infrastructure;

import com.damian.xBank.modules.banking.account.domain.event.DepositCompletedEvent;
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
public class BankingAccountEventListener {
    private final NotificationPublisher notificationPublisher;

    public BankingAccountEventListener(
        NotificationPublisher notificationPublisher
    ) {
        this.notificationPublisher = notificationPublisher;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDepositCompleted(DepositCompletedEvent event) {
        notificationPublisher.publish(
            new NotificationEvent(
                event.accountId(),
                NotificationType.TRANSACTION,
                Map.of(
                    "transactionId", event.transactionId(),
                    "depositor", event.depositor(),
                    "amount", event.depositAmount(),
                    "currency", event.currency()
                ),
                "notification.account.deposit.completed",
                Instant.now()
            )
        );
    }
}