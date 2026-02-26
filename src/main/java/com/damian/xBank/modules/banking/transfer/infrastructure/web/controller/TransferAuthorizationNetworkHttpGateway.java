package com.damian.xBank.modules.banking.transfer.infrastructure.web.controller;

import com.damian.xBank.modules.banking.transfer.application.TransferAuthorizationNetworkGateway;
import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.TransferAuthorizationNetworkRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.TransferAuthorizationNetworkResponse;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TransferAuthorizationNetworkHttpGateway implements TransferAuthorizationNetworkGateway {
    private static final Logger log = LoggerFactory.getLogger(TransferAuthorizationNetworkHttpGateway.class);
    private final WebClient webClient;

    @Value("${transfer-network.endpoint}")
    private String paymentNetworkEndpoint;

    public TransferAuthorizationNetworkHttpGateway(
        WebClient.Builder builder,
        @Value("${transfer-network.base-url}")
        String paymentNetworkBaseUrl
    ) {
        this.webClient = builder
            .baseUrl(paymentNetworkBaseUrl)
            .build();
    }

    @Override
    public TransferAuthorizationNetworkResponse authorizeTransfer(
        TransferAuthorizationNetworkRequest request
    ) {
        return webClient
            .post()
            .uri(paymentNetworkEndpoint)
            .bodyValue(request)
            .exchangeToMono(response -> {
                // response if error
                if (response.statusCode().isError()) {
                    return response.bodyToMono(ApiResponse.class)
                        .map(body -> new TransferAuthorizationNetworkResponse(
                            null,
                            TransferAuthorizationStatus.REJECTED,
                            body.getMessage()
                        ));
                }

                // response if success
                return response.bodyToMono(TransferAuthorizationNetworkResponse.class);
            })
            .block();
    }
}