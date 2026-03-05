package com.damian.xBank.modules.banking.card.application.cqrs.command;

import java.math.BigDecimal;

public record WithdrawFromATMCommand(
    Long cardId,
    BigDecimal amount,
    String pin
) {
}
