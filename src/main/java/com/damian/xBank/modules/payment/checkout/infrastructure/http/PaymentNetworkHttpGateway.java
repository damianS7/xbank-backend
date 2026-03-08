package com.damian.xBank.modules.payment.checkout.infrastructure.http;

import com.damian.xBank.modules.payment.checkout.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentNetworkHttpGateway implements PaymentNetworkGateway {
    private static final Logger log = LoggerFactory.getLogger(PaymentNetworkHttpGateway.class);
    private final WebClient webClient;

    @Value("${payment-network.endpoint}")
    private String paymentNetworkEndpoint;

    public PaymentNetworkHttpGateway(
        WebClient.Builder builder,
        @Value("${payment-network.base-url}")
        String paymentNetworkBaseUrl
    ) {
        this.webClient = builder
            .baseUrl(paymentNetworkBaseUrl)
            .build();
    }

    @Override
    public PaymentAuthorizationResponse authorizePayment(PaymentAuthorizationRequest request) {
        return webClient
            .post()
            .uri(paymentNetworkEndpoint)
            .bodyValue(request)
            .exchangeToMono(response -> {
                // response if error
                if (response.statusCode().isError()) {
                    return response.bodyToMono(ApiResponse.class)
                        .map(body -> new PaymentAuthorizationResponse(
                            PaymentAuthorizationStatus.DECLINED,
                            null,
                            body.getMessage()
                        ));
                }

                // response if success
                return response.bodyToMono(PaymentAuthorizationResponse.class);
            })
            .block();
    }
}