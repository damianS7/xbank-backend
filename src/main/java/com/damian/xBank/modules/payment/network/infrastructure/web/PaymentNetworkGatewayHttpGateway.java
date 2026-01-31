package com.damian.xBank.modules.payment.network.infrastructure.web;

import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;
import com.damian.xBank.shared.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PaymentNetworkGatewayHttpGateway implements PaymentNetworkGateway {
    private static final Logger log = LoggerFactory.getLogger(PaymentNetworkGatewayHttpGateway.class);
    private final WebClient webClient;

    @Value("${payment-network.endpoint}")
    private String paymentNetworkEndpoint;

    public PaymentNetworkGatewayHttpGateway(
            WebClient.Builder builder,
            @Value("${payment-network.base-url}")
            String paymentNetworkBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(paymentNetworkBaseUrl)
                .build();
    }

    @Override
    public PaymentAuthorizationResponse authorizePayment(
            PaymentAuthorizationRequest request
    ) {
        return webClient
                .post()
                .uri(paymentNetworkEndpoint)
                .bodyValue(request)
                //                .retrieve()
                //                .bodyToMono(PaymentAuthorizationResponse.class)
                //                .block();
                .exchangeToMono(response -> {
                    // TODO response ApiResponse???
                    //                    throw new RuntimeException("Unexpected response from payment network");
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(ApiResponse.class)
                                       .map(body -> new PaymentAuthorizationResponse(
                                               PaymentAuthorizationStatus.DECLINED,
                                               null,
                                               body.getMessage()
                                       ));
                    }

                    return response.bodyToMono(PaymentAuthorizationResponse.class);
                })
                .block();
    }
}