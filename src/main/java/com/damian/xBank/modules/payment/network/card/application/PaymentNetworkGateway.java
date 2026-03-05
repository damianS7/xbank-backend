package com.damian.xBank.modules.payment.network.card.application;

import com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.response.PaymentAuthorizationResult;

public interface PaymentNetworkGateway {
    PaymentAuthorizationResult authorizePayment(PaymentAuthorizationRequest request);

    //    void capturePayment(); TODO
}
