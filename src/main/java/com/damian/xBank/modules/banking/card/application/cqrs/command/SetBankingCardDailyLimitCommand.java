package com.damian.xBank.modules.banking.card.application.cqrs.command;

import java.math.BigDecimal;

public record SetBankingCardDailyLimitCommand(
    Long cardId,
    BigDecimal dailyLimit,
    String password
) {
}
