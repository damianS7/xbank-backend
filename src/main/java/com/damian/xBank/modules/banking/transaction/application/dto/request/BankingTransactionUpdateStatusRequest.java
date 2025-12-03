package com.damian.xBank.modules.banking.transaction.application.dto.request;

import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;

public record BankingTransactionUpdateStatusRequest(
        BankingTransactionStatus transactionStatus
) {
}
