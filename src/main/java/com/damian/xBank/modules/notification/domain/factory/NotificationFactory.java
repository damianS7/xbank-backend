package com.damian.xBank.modules.notification.domain.factory;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class NotificationFactory {

    public NotificationEvent transferSent(BankingTransfer transfer) {
        return new NotificationEvent(
                transfer.getToAccount().getOwner().getId(),
                NotificationType.TRANSFER_SENT,
                Map.of(
                        "transactionId", transfer.getFromTransaction().getId(),
                        "toUser", transfer.getToAccount().getOwner().getProfile().getFullName(),
                        "amount", transfer.getAmount(),
                        "currency", transfer.getFromAccount().getCurrency(),
                        "messageCode", "notification.transfer.sent"
                ),
                Instant.now()
        );
    }

    public NotificationEvent transferReceived(BankingTransfer transfer) {
        return new NotificationEvent(
                transfer.getToAccount().getOwner().getId(),
                NotificationType.TRANSFER_RECEIVED,
                Map.of(
                        "transactionId", transfer.getToTransaction().getId(),
                        "fromUser", transfer.getFromAccount().getOwner().getProfile().getFullName(),
                        "amount", transfer.getAmount(),
                        "currency", transfer.getFromAccount().getCurrency(),
                        "messageCode", "notification.transfer.received"
                ),
                Instant.now()
        );
    }
}
