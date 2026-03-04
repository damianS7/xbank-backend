package com.damian.xBank.modules.banking.account.application.cqrs.result;

import java.util.Set;

public record DailyBalancesByCurrencyResult(
    Set<Object> dailyBalances
) {
}
