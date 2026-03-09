package com.damian.xBank.modules.banking.transaction.application.usecase.get.account;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import org.springframework.data.domain.Page;

public record GetAccountTransactionsResult(
    Page<BankingTransactionResult> pagedTransactions
) {
}
