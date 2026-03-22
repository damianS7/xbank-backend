package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.fail;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;

import java.math.BigDecimal;

public record FailedOutgoingTransferResult(
    Long id,
    BigDecimal amount,
    OutgoingTransferStatus status,
    OutgoingTransferType type,
    String failureReason
) {
    public static FailedOutgoingTransferResult from(OutgoingTransfer transfer) {
        return new FailedOutgoingTransferResult(
            transfer.getId(),
            transfer.getAmount(),
            transfer.getStatus(),
            transfer.getType(),
            transfer.getFromTransaction().getDescription()
        );
    }
}
