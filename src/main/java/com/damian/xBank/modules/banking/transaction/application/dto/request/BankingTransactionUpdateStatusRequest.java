package com.damian.xBank.modules.banking.transaction.application.dto.request;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;

public record BankingTransactionUpdateStatusRequest(
        BankingTransactionStatus transactionStatus
) {
}
