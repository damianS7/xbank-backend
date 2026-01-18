package com.damian.xBank.modules.banking.payment.application.dto.mapper;

import com.damian.xBank.modules.banking.payment.application.dto.response.PaymentDto;
import com.damian.xBank.modules.banking.payment.domain.model.Payment;

public class PaymentDtoMapper {
    public static PaymentDto toPaymentDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getStatus(),
                payment.getMerchant(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
