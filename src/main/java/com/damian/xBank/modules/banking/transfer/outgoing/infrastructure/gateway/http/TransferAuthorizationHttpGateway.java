package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.gateway.http;

import com.damian.xBank.modules.banking.transfer.outgoing.application.TransferAuthorizationGateway;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.TransferAuthorizationStatus;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response.TransferAuthorizationResponse;
import com.damian.xBank.shared.infrastructure.web.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TransferAuthorizationHttpGateway implements TransferAuthorizationGateway {
    private static final Logger log = LoggerFactory.getLogger(TransferAuthorizationHttpGateway.class);
    private final RestClient restClient;

    @Value("${transfer-network.endpoint}")
    private String paymentNetworkEndpoint;

    public TransferAuthorizationHttpGateway(
        RestClient.Builder builder,
        @Value("${transfer-network.base-url}") String paymentNetworkBaseUrl
    ) {
        this.restClient = builder
            .baseUrl(paymentNetworkBaseUrl)
            .build();
    }

    @Override
    public TransferAuthorizationResponse authorizeTransfer(
        TransferAuthorizationRequest request
    ) {
        return restClient
            .post()
            .uri(paymentNetworkEndpoint)
            .body(request)
            .exchange((req, response) -> {
                // response if error
                if (response.getStatusCode().isError()) {
                    ApiResponse apiResponse = response.bodyTo(ApiResponse.class);
                    return new TransferAuthorizationResponse(
                        null,
                        TransferAuthorizationStatus.REJECTED,
                        apiResponse != null ? apiResponse.getMessage() : "Unknown error"
                    );
                }

                // response if success
                return response.bodyTo(TransferAuthorizationResponse.class);
            });
    }
}