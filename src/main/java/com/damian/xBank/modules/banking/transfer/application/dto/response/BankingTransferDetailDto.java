package com.damian.xBank.modules.banking.transfer.application.dto.response;

import com.damian.xBank.modules.banking.transaction.application.cqrs.result.BankingTransactionResult;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransferDetailDto(
    Long id,
    Long fromAccountId,
    String toAccountNumber,
    BigDecimal amount,
    BankingTransferStatus status,
    String description,
    BankingTransactionResult transaction,
    Instant createdAt,
    Instant updatedAt
) {
}
