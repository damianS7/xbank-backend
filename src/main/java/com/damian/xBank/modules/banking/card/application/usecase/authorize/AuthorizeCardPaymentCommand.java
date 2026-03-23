package com.damian.xBank.modules.banking.card.application.usecase.authorize;

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
