package com.damian.xBank.modules.banking.card.application.cqrs.command;

import java.math.BigDecimal;

public record AuthorizeCardPaymentCommand(
    String merchant,
    String cardHolder,
    String cardNumber,
    Integer expiryMonth,
    Integer expiryYear,
    String cvv,
    BigDecimal amount
) {
}
