package com.damian.xBank.modules.payment.network.card.application;

import com.damian.xBank.modules.payment.network.card.application.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.card.application.dto.response.PaymentAuthorizationResponse;

public interface PaymentNetworkGateway {
    PaymentAuthorizationResponse authorizePayment(PaymentAuthorizationRequest request);

    //    void capturePayment();


}
