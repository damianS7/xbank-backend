package com.damian.xBank.modules.payment.intent.application.dto.mapper;

import com.damian.xBank.modules.payment.intent.application.dto.response.PaymentOrderDto;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;

public class PaymentIntentOrderDtoMapper {
    public static PaymentOrderDto toPaymentDto(PaymentIntent paymentIntent) {
        return new PaymentOrderDto(
                paymentIntent.getId(),
                paymentIntent.getStatus(),
                paymentIntent.getMerchantName(),
                paymentIntent.getAmount(),
                paymentIntent.getCurrency(),
                paymentIntent.getCreatedAt(),
                paymentIntent.getUpdatedAt()
        );
    }
}
