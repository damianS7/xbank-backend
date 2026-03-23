package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.create;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferType;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateOutgoingTransferResult(
    Long id,
    Long fromAccountId,
    String toAccountNumber,
    BigDecimal amount,
    OutgoingTransferStatus status,
    OutgoingTransferType type,
    String description,
    BankingTransactionResult transaction,
    Instant createdAt,
    Instant updatedAt
) {
    public static CreateOutgoingTransferResult from(OutgoingTransfer transfer) {
        return new CreateOutgoingTransferResult(
            transfer.getId(),
            transfer.getFromAccount().getId(),
            transfer.getToAccountIban(),
            transfer.getAmount(),
            transfer.getStatus(),
            transfer.getType(),
            transfer.getDescription(),
            BankingTransactionDtoMapper.toBankingTransactionResult(transfer.getFromTransaction()),
            transfer.getCreatedAt(),
            transfer.getUpdatedAt()
        );
    }
}
