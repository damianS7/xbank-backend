package com.damian.xBank.modules.banking.transaction.application.dto.mapper;

import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Set;
import java.util.stream.Collectors;

public class BankingTransactionDtoMapper {


    public static BankingTransactionDto toBankingTransactionDto(BankingTransaction accountTransaction) {
        return new BankingTransactionDto(
                accountTransaction.getId(),
                accountTransaction.getAssociatedBankingAccount().getId(),
                accountTransaction.getBankingCard() != null ? accountTransaction.getBankingCard().getId() : null,
                accountTransaction.getAmount(),
                //                accountTransaction.getAssociatedBankingAccount().getBalance(),
                accountTransaction.getAssociatedBankingAccount().getAccountCurrency(),
                accountTransaction.getLastBalance(),
                accountTransaction.getType(),
                accountTransaction.getStatus(),
                accountTransaction.getDescription(),
                accountTransaction.getCreatedAt(),
                accountTransaction.getUpdatedAt()
        );
    }

    public static Set<BankingTransactionDto> toBankingTransactionSetDTO(Set<BankingTransaction> accountTransactions) {
        return accountTransactions.stream().map(
                BankingTransactionDtoMapper::toBankingTransactionDto
        ).collect(Collectors.toSet());
    }

    public static Page<BankingTransactionDto> toBankingTransactionPageDto(Page<BankingTransaction> accountTransactions) {
        return accountTransactions.map(
                BankingTransactionDtoMapper::toBankingTransactionDto
        );
    }

    public static Page<BankingTransactionDto> toBankingTransactionPageDto(Set<BankingTransaction> accountTransactions) {
        return new PageImpl<>(
                accountTransactions.stream().map(
                        BankingTransactionDtoMapper::toBankingTransactionDto
                ).collect(Collectors.toList())
        );
    }

}
