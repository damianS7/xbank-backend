package com.damian.xBank.modules.banking.transactions.http;

import com.damian.xBank.modules.banking.transactions.BankingTransactionStatus;

public record BankingTransactionUpdateStatusRequest(
        BankingTransactionStatus transactionStatus
) {
}
