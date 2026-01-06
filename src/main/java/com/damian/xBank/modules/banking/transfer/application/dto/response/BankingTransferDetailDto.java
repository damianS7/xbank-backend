package com.damian.xBank.modules.banking.transfer.application.dto.response;

import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
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
        BankingTransactionDto transaction,
        Instant createdAt,
        Instant updatedAt
) {
}
