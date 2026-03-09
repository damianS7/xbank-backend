package com.damian.xBank.modules.banking.transfer.application.dto;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferType;

import java.math.BigDecimal;
import java.time.Instant;

public record BankingTransferResult(
    Long id,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    String currency,
    BankingTransferStatus status,
    BankingTransferType type,
    String description,
    Instant createdAt
) {
    public static BankingTransferResult from(BankingTransfer bankingTransfer) {
        return new BankingTransferResult(
            bankingTransfer.getId(),
            bankingTransfer.getFromAccount().getAccountNumber(),
            bankingTransfer.getToAccountIban(),
            bankingTransfer.getAmount(),
            bankingTransfer.getFromAccount().getCurrency().toString(),
            bankingTransfer.getStatus(),
            bankingTransfer.getType(),
            bankingTransfer.getDescription(),
            bankingTransfer.getCreatedAt()
        );
    }
}
