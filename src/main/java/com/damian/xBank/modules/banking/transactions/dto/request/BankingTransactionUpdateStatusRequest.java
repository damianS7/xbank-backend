package com.damian.xBank.modules.banking.transactions.dto.request;

import com.damian.xBank.modules.banking.transactions.enums.BankingTransactionStatus;

public record BankingTransactionUpdateStatusRequest(
        BankingTransactionStatus transactionStatus
) {
}
