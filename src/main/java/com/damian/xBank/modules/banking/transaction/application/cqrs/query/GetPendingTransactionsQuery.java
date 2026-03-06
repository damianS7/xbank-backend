package com.damian.xBank.modules.banking.transaction.application.cqrs.query;

import org.springframework.data.domain.Pageable;

public record GetPendingTransactionsQuery(
    Pageable pageable
) {
}
