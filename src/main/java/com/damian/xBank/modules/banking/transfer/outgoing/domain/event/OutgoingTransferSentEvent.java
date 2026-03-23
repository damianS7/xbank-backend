package com.damian.xBank.modules.banking.transfer.outgoing.domain.event;

import java.math.BigDecimal;

public record OutgoingTransferSentEvent(
    Long transferId,
    Long toUserId,
    Long transactionId,
    String toUser,
    BigDecimal amount,
    String currency
) {
}