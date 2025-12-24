package com.damian.xBank.modules.banking.transfer.application.dto.mapper;

import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDto;
import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;

public class BankingTransferDtoMapper {
    public static BankingTransferDto toBankingTransferDto(BankingTransfer bankingTransfer) {

        return new BankingTransferDto(
                bankingTransfer.getId(),
                bankingTransfer.getFromAccount().getId(),
                bankingTransfer.getToAccount().getAccountNumber(),
                bankingTransfer.getAmount(),
                bankingTransfer.getStatus(),
                bankingTransfer.getCreatedAt(),
                bankingTransfer.getUpdatedAt()
        );
    }
}
