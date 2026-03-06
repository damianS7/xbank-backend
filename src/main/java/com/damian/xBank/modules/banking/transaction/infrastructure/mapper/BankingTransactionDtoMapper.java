package com.damian.xBank.modules.banking.transaction.infrastructure.mapper;

import com.damian.xBank.modules.banking.transaction.application.cqrs.result.BankingTransactionDetailResult;
import com.damian.xBank.modules.banking.transaction.application.cqrs.result.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Set;
import java.util.stream.Collectors;

public class BankingTransactionDtoMapper {


    public static BankingTransactionResult toBankingTransactionResult(BankingTransaction transaction) {
        return new BankingTransactionResult(
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

    public static BankingTransactionDetailResult toBankingTransactionDetailDto(BankingTransaction transaction) {
        return new BankingTransactionDetailResult(
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

    public static Set<BankingTransactionResult> toBankingTransactionResultSet(Set<BankingTransaction> accountTransactions) {
        return accountTransactions.stream().map(
            BankingTransactionDtoMapper::toBankingTransactionResult
        ).collect(Collectors.toSet());
    }

    public static Page<BankingTransactionResult> toBankingTransactionPagedResult(Page<BankingTransaction> accountTransactions) {
        return accountTransactions.map(
            BankingTransactionDtoMapper::toBankingTransactionResult
        );
    }

    public static Page<BankingTransactionResult> toBankingTransactionPagedResult(Set<BankingTransaction> accountTransactions) {
        return new PageImpl<>(
            accountTransactions.stream().map(
                BankingTransactionDtoMapper::toBankingTransactionResult
            ).collect(Collectors.toList())
        );
    }

}
