package com.damian.xBank.modules.notification.domain.factory;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class NotificationFactory {

    public NotificationEvent cardPaymentCompleted(BankingTransaction transaction) {
        return new NotificationEvent(
                transaction.getBankingCard().getOwner().getId(),
                NotificationType.CARD_PAYMENT_COMPLETED,
                Map.of(
                        "transactionId", transaction.getId(),
                        "shopName", transaction.getDescription(),
                        "amount", transaction.getAmount(),
                        "currency", transaction.getBankingAccount().getCurrency(),
                        "messageCode", "notification.card.payment.completed"
                ),
                Instant.now()
        );
    }

    public NotificationEvent depositCompleted(BankingTransaction transaction) {
        return new NotificationEvent(
                transaction.getBankingCard().getOwner().getId(),
                NotificationType.DEPOSIT_COMPLETED,
                Map.of(
                        "transactionId", transaction.getId(),
                        "toUser", transaction.getBankingCard().getOwner().getProfile().getFullName(),
                        "amount", transaction.getAmount(),
                        "currency", transaction.getBankingAccount().getCurrency(),
                        "messageCode", "notification.account.deposit.completed"
                ),
                Instant.now()
        );
    }

    public NotificationEvent withdrawCompleted(BankingTransaction transaction) {
        return new NotificationEvent(
                transaction.getBankingCard().getOwner().getId(),
                NotificationType.WITHDRAWAL_COMPLETED,
                Map.of(
                        "transactionId", transaction.getId(),
                        "toUser", transaction.getBankingCard().getOwner().getProfile().getFullName(),
                        "amount", transaction.getAmount(),
                        "currency", transaction.getBankingAccount().getCurrency(),
                        "messageCode", "notification.card.withdraw.completed"
                ),
                Instant.now()
        );
    }

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
