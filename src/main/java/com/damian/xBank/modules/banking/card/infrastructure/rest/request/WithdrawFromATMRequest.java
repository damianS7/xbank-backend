package com.damian.xBank.modules.banking.card.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WithdrawFromATMRequest(
    @Positive
    BigDecimal amount,

    @NotBlank
    @Size(min = 4, max = 4)
    String cardPIN
) {
}
