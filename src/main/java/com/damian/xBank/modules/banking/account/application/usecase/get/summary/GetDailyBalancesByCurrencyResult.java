package com.damian.xBank.modules.banking.account.application.usecase.get.summary;

import java.util.Set;

public record GetDailyBalancesByCurrencyResult(
    Set<Object> dailyBalances
) {
}
