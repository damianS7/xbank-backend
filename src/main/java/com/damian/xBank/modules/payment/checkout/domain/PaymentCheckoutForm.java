package com.damian.xBank.modules.payment.checkout.domain;

import jakarta.validation.constraints.*;

public record PaymentCheckoutForm(

        @NotNull(message = "Payment id is required")
        @Positive(message = "Payment id must be a positive number")
        Long paymentId,

        @NotBlank(message = "Card holder is required")
        String cardHolder,

        @NotBlank(message = "Card number is required")
        @Pattern(
                regexp = "\\d{16}",
                message = "Card number must contain exactly 16 digits"
        )
        String cardNumber,

        @NotBlank(message = "Card PIN is required")
        @Pattern(
                regexp = "\\d{4}",
                message = "Card PIN must contain exactly 4 digits"
        )
        String cardPin,

        @NotBlank(message = "CVV is required")
        @Pattern(
                regexp = "\\d{3}",
                message = "CVV must contain exactly 3 digits"
        )
        String cvv,

        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry year is required")
        @Min(value = 2020, message = "Expiry year must be greater than or equal to 2020")
        @Max(value = 2999, message = "Expiry year must be less than or equal to 2999")
        Integer expiryYear
) {
    public PaymentCheckoutForm() {
        this(null, null, null, null, null, null, null);
    }
}