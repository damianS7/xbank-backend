package com.damian.xBank.modules.banking.transfer.application.dto.mapper;

import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDetailDto;
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
                bankingTransfer.getDescription(),
                bankingTransfer.getCreatedAt(),
                bankingTransfer.getUpdatedAt()
        );
    }

    public static BankingTransferDetailDto toBankingTransferDetailDto(BankingTransfer bankingTransfer) {

        return new BankingTransferDetailDto(
                bankingTransfer.getId(),
                bankingTransfer.getFromAccount().getId(),
                bankingTransfer.getToAccount().getAccountNumber(),
                bankingTransfer.getAmount(),
                bankingTransfer.getStatus(),
                bankingTransfer.getDescription(),
                BankingTransactionDtoMapper.toBankingTransactionDto(bankingTransfer.getFromTransaction()),
                bankingTransfer.getCreatedAt(),
                bankingTransfer.getUpdatedAt()
        );
    }
}
