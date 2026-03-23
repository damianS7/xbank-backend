package com.damian.xBank.modules.payment.intent.application.usecase.create;

import java.math.BigDecimal;

public record CreatePaymentIntentCommand(
    String orderId,
    String description,
    BigDecimal amount,
    String currency
) {
}
