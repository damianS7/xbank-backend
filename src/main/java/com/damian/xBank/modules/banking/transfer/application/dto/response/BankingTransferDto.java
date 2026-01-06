package com.damian.xBank.modules.banking.transfer.application.dto.response;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransferDto(
        Long id,
        Long fromAccountId,
        String toAccountNumber,
        BigDecimal amount,
        BankingTransferStatus status,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
