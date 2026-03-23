package com.damian.xBank.modules.banking.transfer.outgoing.domain.event;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;

import java.math.BigDecimal;

public record OutgoingTransferCompletedEvent(
    Long transferId,
    OutgoingTransferType transferType,

    Long senderUserId,
    String senderUserName,
    Long senderTransactionId,

    Long recipientUserId,
    String recipientUserName,
    Long recipientTransactionId,

    BigDecimal amount,
    String currency
) {
}