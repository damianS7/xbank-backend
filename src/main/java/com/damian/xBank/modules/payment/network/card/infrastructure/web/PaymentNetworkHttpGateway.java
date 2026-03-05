package com.damian.xBank.modules.payment.network.card.infrastructure.web;

import com.damian.xBank.modules.payment.network.card.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.card.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.card.infrastructure.web.dto.response.PaymentAuthorizationResult;
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
    public PaymentAuthorizationResult authorizePayment(
        PaymentAuthorizationRequest request
    ) {
        return webClient
            .post()
            .uri(paymentNetworkEndpoint)
            .bodyValue(request)
            .exchangeToMono(response -> {
                // response if error
                if (response.statusCode().isError()) {
                    return response.bodyToMono(ApiResponse.class)
                        .map(body -> new PaymentAuthorizationResult(
                            PaymentAuthorizationStatus.DECLINED,
                            null,
                            body.getMessage()
                        ));
                }

                // response if success
                return response.bodyToMono(PaymentAuthorizationResult.class);
            })
            .block();
    }
}