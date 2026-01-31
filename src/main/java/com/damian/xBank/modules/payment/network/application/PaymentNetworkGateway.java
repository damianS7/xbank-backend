package com.damian.xBank.modules.payment.network.application;

import com.damian.xBank.modules.payment.network.application.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;

public interface PaymentNetworkGateway {
    PaymentAuthorizationResponse authorizePayment(PaymentAuthorizationRequest request);

    //    void capturePayment();


}
