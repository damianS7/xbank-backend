package com.damian.xBank.modules.banking.transfer.infrastructure.gateway.http;

import com.damian.xBank.modules.banking.transfer.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.response.TransferAuthorizationResponse;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

// TODO migrate to RestClient
@Component
public class TransferAuthorizationHttpGateway implements TransferAuthorizationGateway {
    private static final Logger log = LoggerFactory.getLogger(TransferAuthorizationHttpGateway.class);
    private final WebClient webClient;

    @Value("${transfer-network.endpoint}")
    private String paymentNetworkEndpoint;

    public TransferAuthorizationHttpGateway(
        WebClient.Builder builder,
        @Value("${transfer-network.base-url}")
        String paymentNetworkBaseUrl
    ) {
        this.webClient = builder
            .baseUrl(paymentNetworkBaseUrl)
            .build();
    }

    @Override
    public TransferAuthorizationResponse authorizeTransfer(
        TransferAuthorizationRequest request
    ) {
        return webClient
            .post()
            .uri(paymentNetworkEndpoint)
            .bodyValue(request)
            .exchangeToMono(response -> {
                // response if error
                if (response.statusCode().isError()) {
                    return response.bodyToMono(ApiResponse.class)
                        .map(body -> new TransferAuthorizationResponse(
                            null,
                            TransferAuthorizationStatus.REJECTED,
                            body.getMessage()
                        ));
                }

                // response if success
                return response.bodyToMono(TransferAuthorizationResponse.class);
            })
            .block();
    }
}