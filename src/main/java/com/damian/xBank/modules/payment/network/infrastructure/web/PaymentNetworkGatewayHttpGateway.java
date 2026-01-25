package com.damian.xBank.modules.payment.network.infrastructure.web;

import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class PaymentNetworkGatewayHttpGateway implements PaymentNetworkGateway {
    private final WebClient webClient;

    public PaymentNetworkGatewayHttpGateway(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8079/")
                .build();
    }

    @Override
    public PaymentAuthorizationResponse authorizePayment(
            String cardNumber,
            String cardCvv,
            String cardPin,
            BigDecimal amount,
            String merchant
    ) {

        PaymentAuthorizationRequest request = new PaymentAuthorizationRequest(
                merchant, cardNumber, 1, 2024, cardCvv, cardPin, amount
        );

        return webClient
                .post()
                .uri("/api/v1/cards/authorize")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentAuthorizationResponse.class)
                .block();
    }
}