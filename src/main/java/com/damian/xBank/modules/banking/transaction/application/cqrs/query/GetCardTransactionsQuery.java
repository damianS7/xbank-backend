package com.damian.xBank.modules.banking.transaction.application.cqrs.query;

import org.springframework.data.domain.Pageable;

public record GetCardTransactionsQuery(
    Long cardId,
    Pageable pageable
) {
}
