package com.damian.xBank.modules.banking.transaction.application.usecase.get.card;

import org.springframework.data.domain.Pageable;

public record GetCardTransactionsQuery(
    Long cardId,
    Pageable pageable
) {
}
