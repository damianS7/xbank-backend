package com.damian.xBank.modules.banking.transfer.outgoing.application.usecase.authorize;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AuthorizeOutgoingTransferResult(
    Long id,
    Long fromAccountId,
    String toAccountNumber,
    BigDecimal amount,
    OutgoingTransferStatus status,
    String description,
    BankingTransactionResult transaction,
    Instant createdAt,
    Instant updatedAt
) {
    public static AuthorizeOutgoingTransferResult from(OutgoingTransfer transfer) {
        return new AuthorizeOutgoingTransferResult(
            transfer.getId(),
            transfer.getFromAccount().getId(),
            transfer.getToAccountIban(),
            transfer.getAmount(),
            transfer.getStatus(),
            transfer.getDescription(),
            BankingTransactionDtoMapper.toBankingTransactionResult(transfer.getFromTransaction()),
            transfer.getCreatedAt(),
            transfer.getUpdatedAt()
        );
    }
}
