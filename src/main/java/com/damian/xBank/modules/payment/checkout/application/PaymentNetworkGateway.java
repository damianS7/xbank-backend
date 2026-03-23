package com.damian.xBank.modules.payment.checkout.application;

import com.damian.xBank.modules.payment.checkout.infrastructure.http.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;

public interface PaymentNetworkGateway {
    PaymentAuthorizationResponse authorizePayment(PaymentAuthorizationRequest request);

    //    void capturePayment(); TODO
}
