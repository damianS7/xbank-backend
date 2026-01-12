package com.damian.xBank.modules.banking.transfer.application.mapper;

import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDetailDto;
import com.damian.xBank.modules.banking.transfer.application.dto.response.BankingTransferDto;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Set;
import java.util.stream.Collectors;

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

    public static Set<BankingTransferDto> toBankingTransferDtoSet(Set<BankingTransfer> transfers) {

        return transfers.stream()
                        .map(BankingTransferDtoMapper::toBankingTransferDto)
                        .collect(Collectors.toSet());
    }

    public static Page<BankingTransferDto> toBankingTransferDtoPage(Page<BankingTransfer> transfers) {

        return new PageImpl<>(
                transfers.stream().map(
                        BankingTransferDtoMapper::toBankingTransferDto
                ).collect(Collectors.toList())
        );
    }
}
