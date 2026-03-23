package com.damian.xBank.modules.banking.card.application.usecase.withdraw;

import java.math.BigDecimal;

public record WithdrawFromATMCommand(
    Long cardId,
    BigDecimal amount,
    String pin
) {
}
