package com.damian.xBank.modules.banking.account.application.usecase.account.get.summary;

import java.util.Set;

public record GetDailyBalancesByCurrencyResult(
    Set<Object> dailyBalances
) {
}
