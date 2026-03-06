package com.damian.xBank.modules.banking.transaction.application.cqrs.result;

import org.springframework.data.domain.Page;

public record GetCardTransactionsResult(
    Page<BankingTransactionResult> pagedTransactions
) {
}
