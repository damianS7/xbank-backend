package com.damian.xBank.modules.banking.transfer.outgoing.application.dto;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OutgoingTransferResult(
    Long id,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    String currency,
    OutgoingTransferStatus status,
    OutgoingTransferType type,
    String description,
    Instant createdAt
) {
    public static OutgoingTransferResult from(OutgoingTransfer outgoingTransfer) {
        return new OutgoingTransferResult(
            outgoingTransfer.getId(),
            outgoingTransfer.getFromAccount().getAccountNumber(),
            outgoingTransfer.getToAccountIban(),
            outgoingTransfer.getAmount(),
            outgoingTransfer.getFromAccount().getCurrency().toString(),
            outgoingTransfer.getStatus(),
            outgoingTransfer.getType(),
            outgoingTransfer.getDescription(),
            outgoingTransfer.getCreatedAt()
        );
    }
}
