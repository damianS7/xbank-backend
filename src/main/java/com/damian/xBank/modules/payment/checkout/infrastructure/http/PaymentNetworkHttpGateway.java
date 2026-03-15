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
import org.springframework.web.client.RestClient;

@Component
public class PaymentNetworkHttpGateway implements PaymentNetworkGateway {
    private static final Logger log = LoggerFactory.getLogger(PaymentNetworkHttpGateway.class);
    private final RestClient restClient;

    @Value("${payment-network.endpoint}")
    private String paymentNetworkEndpoint;

    public PaymentNetworkHttpGateway(
        RestClient.Builder builder,
        @Value("${payment-network.base-url}") String paymentNetworkBaseUrl
    ) {
        this.restClient = builder
            .baseUrl(paymentNetworkBaseUrl)
            .build();
    }

    @Override
    public PaymentAuthorizationResponse authorizePayment(PaymentAuthorizationRequest request) {
        return restClient
            .post()
            .uri(paymentNetworkEndpoint)
            .body(request)
            .exchange((req, response) -> {
                // response if error
                if (response.getStatusCode().isError()) {
                    ApiResponse apiResponse = response.bodyTo(ApiResponse.class);
                    return new PaymentAuthorizationResponse(
                        PaymentAuthorizationStatus.DECLINED,
                        null,
                        apiResponse != null ? apiResponse.getMessage() : "Unknown error"
                    );
                }

                // response if success
                return response.bodyTo(PaymentAuthorizationResponse.class);
            });
    }
}