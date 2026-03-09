package com.damian.xBank.modules.banking.transaction.application.usecase.get.card;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import org.springframework.data.domain.Page;

public record GetCardTransactionsResult(
    Page<BankingTransactionResult> pagedTransactions
) {
}
