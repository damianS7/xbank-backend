package com.damian.xBank.modules.banking.transaction.dto.request;

import com.damian.xBank.modules.banking.transaction.enums.BankingTransactionStatus;

public record BankingTransactionUpdateStatusRequest(
        BankingTransactionStatus transactionStatus
) {
}
