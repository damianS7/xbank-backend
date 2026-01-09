package com.damian.xBank.modules.notification.domain.model;

public enum NotificationType {
    TRANSFER_RECEIVED,
    TRANSFER_SENT,
    DEPOSIT_COMPLETED,
    WITHDRAWAL_COMPLETED,
    CARD_PAYMENT_DECLINED,
    CARD_PAYMENT_PENDING,
    CARD_EXPIRING_SOON,
    ACCOUNT_CLOSED,
}
