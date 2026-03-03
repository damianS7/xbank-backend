package com.damian.xBank.modules.payment.network.card.application;

import com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.response.PaymentAuthorizationResponse;

public interface PaymentNetworkGateway {
    PaymentAuthorizationResponse authorizePayment(PaymentAuthorizationRequest request);

    //    void capturePayment(); TODO
}
