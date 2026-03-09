package com.damian.xBank.modules.banking.card.application.usecase.set.limit;

import java.math.BigDecimal;

public record SetBankingCardDailyLimitCommand(
    Long cardId,
    BigDecimal dailyLimit,
    String password
) {
}
