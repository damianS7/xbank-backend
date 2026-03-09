package com.damian.xBank.modules.banking.transfer.application.usecase.outgoing.create;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateOutgoingTransferResult(
    Long id,
    Long fromAccountId,
    String toAccountNumber,
    BigDecimal amount,
    BankingTransferStatus status,
    BankingTransferType type,
    String description,
    BankingTransactionResult transaction,
    Instant createdAt,
    Instant updatedAt
) {
    public static CreateOutgoingTransferResult from(BankingTransfer transfer) {
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
