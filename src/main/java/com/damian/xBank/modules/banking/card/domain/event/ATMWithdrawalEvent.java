package com.damian.xBank.modules.banking.card.domain.event;

import java.math.BigDecimal;

public record ATMWithdrawalEvent(
    Long transactionId,
    Long cardId,
    String toUser,
    BigDecimal amount,
    String currency
) {
}