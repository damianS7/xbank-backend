package com.damian.xBank.modules.banking.payment.application.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentCreateRequest(
        Long invoiceId,
        String merchant,
        @Positive(message = "")
        BigDecimal amount,
        String currency,
        String returnUrl
) {
}
