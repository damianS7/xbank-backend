package com.damian.xBank.modules.payment.network.transfer.infrastructure.web;

import com.damian.xBank.modules.payment.network.transfer.application.TransferNetworkGateway;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.TransferNetworkAuthorizationRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.response.TransferNetworkAuthorizationResponse;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TransferNetworkHttpGateway implements TransferNetworkGateway {
    private static final Logger log = LoggerFactory.getLogger(TransferNetworkHttpGateway.class);
    private final WebClient webClient;

    @Value("${payment-network.endpoint}")
    private String paymentNetworkEndpoint;

    public TransferNetworkHttpGateway(
        WebClient.Builder builder,
        @Value("${payment-network.base-url}")
        String paymentNetworkBaseUrl
    ) {
        this.webClient = builder
            .baseUrl(paymentNetworkBaseUrl)
            .build();
    }

    @Override
    public TransferNetworkAuthorizationResponse authorizeTransfer(
        TransferNetworkAuthorizationRequest request
    ) {
        return webClient
            .post()
            .uri(paymentNetworkEndpoint)
            .bodyValue(request)
            .exchangeToMono(response -> {
                // response if error
                if (response.statusCode().isError()) {
                    return response.bodyToMono(ApiResponse.class)
                        .map(body -> new TransferNetworkAuthorizationResponse(
                            null,
                            "FAILED",
                            body.getMessage()
                        ));
                }

                // response if success
                return response.bodyToMono(TransferNetworkAuthorizationResponse.class);
            })
            .block();
    }
}