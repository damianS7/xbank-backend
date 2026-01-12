package com.damian.xBank.modules.banking.transaction.application.mapper;

import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDetailDto;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Set;
import java.util.stream.Collectors;

public class BankingTransactionDtoMapper {


    public static BankingTransactionDto toBankingTransactionDto(BankingTransaction transaction) {
        return new BankingTransactionDto(
                transaction.getId(),
                transaction.getBankingAccount().getId(),
                transaction.getBankingCard() != null ? transaction.getBankingCard().getId() : null,
                transaction.getAmount(),
                transaction.getBankingAccount().getCurrency(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    public static BankingTransactionDetailDto toBankingTransactionDetailDto(BankingTransaction transaction) {
        return new BankingTransactionDetailDto(
                transaction.getId(),
                transaction.getBankingAccount().getId(),
                transaction.getTransfer() != null ? transaction
                        .getTransfer()
                        .getFromAccount()
                        .getOwner()
                        .getProfile()
                        .getFullName() : null,
                transaction.getTransfer() != null ? transaction
                        .getTransfer()
                        .getFromAccount()
                        .getAccountNumber() : null,
                transaction.getTransfer() != null ? transaction
                        .getTransfer()
                        .getToAccount()
                        .getOwner()
                        .getProfile()
                        .getFullName() : null,
                transaction.getTransfer() != null ? transaction.getTransfer().getToAccount().getAccountNumber() : null,
                transaction.getBankingCard() != null ? transaction.getBankingCard().getId() : null,
                transaction.getAmount(),
                transaction.getBankingAccount().getCurrency(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
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
