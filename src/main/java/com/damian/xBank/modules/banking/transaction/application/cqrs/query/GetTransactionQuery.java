package com.damian.xBank.modules.banking.transaction.application.cqrs.query;

public record GetTransactionQuery(
    Long transactionId
) {
}
