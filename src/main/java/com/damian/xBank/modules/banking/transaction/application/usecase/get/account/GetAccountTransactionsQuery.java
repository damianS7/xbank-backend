package com.damian.xBank.modules.banking.transaction.application.usecase.get.account;

import org.springframework.data.domain.Pageable;

public record GetAccountTransactionsQuery(
    Long accountId,
    Pageable pageable
) {
}
