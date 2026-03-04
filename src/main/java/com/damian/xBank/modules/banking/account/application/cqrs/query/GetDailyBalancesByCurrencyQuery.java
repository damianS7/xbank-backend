package com.damian.xBank.modules.banking.account.application.cqrs.query;

public record GetDailyBalancesByCurrencyQuery(
    String currency
) {
}
