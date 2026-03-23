package com.damian.xBank.modules.banking.transaction.application.usecase.get.pending;

import org.springframework.data.domain.Pageable;

public record GetPendingTransactionsQuery(
    Pageable pageable
) {
}
