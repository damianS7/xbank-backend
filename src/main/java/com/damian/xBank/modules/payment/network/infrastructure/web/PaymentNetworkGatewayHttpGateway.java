package com.damian.xBank.modules.payment.network.infrastructure.web;

import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class PaymentNetworkGatewayHttpGateway implements PaymentNetworkGateway {
    private static final Logger log = LoggerFactory.getLogger(PaymentNetworkGatewayHttpGateway.class);
    private final WebClient webClient;

    @Value("${payment-network.base-url}")
    private String paymentNetworkBaseUrl;

    @Value("${payment-network.endpoint}")
    private String paymentNetworkEndpoint;

    public PaymentNetworkGatewayHttpGateway(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl(paymentNetworkBaseUrl)
                .build();
    }

    @Override
    public PaymentAuthorizationResponse authorizePayment(
            String cardNumber,
            String cardCvv,
            String cardPin,
            int expiryMonth,
            int expiryYear,
            BigDecimal amount,
            String merchant
    ) {

        PaymentAuthorizationRequest request = new PaymentAuthorizationRequest(
                merchant, cardNumber, expiryMonth, expiryYear, cardCvv, cardPin, amount
        );

        return webClient
                .post()
                .uri(paymentNetworkEndpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentAuthorizationResponse.class)
                .block();
    }
}