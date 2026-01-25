package com.damian.xBank.modules.payment.network.application;

import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;

import java.math.BigDecimal;

public interface PaymentNetworkGateway {
    PaymentAuthorizationResponse authorizePayment(
            String cardNumber,
            String cardCvv,
            String cardPin,
            BigDecimal amount,
            String merchant
    );

    //    void capturePayment();


}
