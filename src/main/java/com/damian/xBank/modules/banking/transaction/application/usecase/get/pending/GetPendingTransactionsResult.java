package com.damian.xBank.modules.banking.transaction.application.usecase.get.pending;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import org.springframework.data.domain.Page;

public record GetPendingTransactionsResult(
    Page<BankingTransactionResult> pagedTransactions
) {
}
