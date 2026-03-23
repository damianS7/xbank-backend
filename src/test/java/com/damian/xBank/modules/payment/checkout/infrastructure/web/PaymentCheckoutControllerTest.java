package com.damian.xBank.modules.payment.checkout.infrastructure.web;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.checkout.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class PaymentCheckoutControllerTest extends AbstractControllerTest {
    @MockitoBean
    private PaymentNetworkGateway paymentNetworkGateway;

    private User customer;
    private PaymentIntent paymentIntent;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
            .withEmail("customer@demo.com")
            .withStatus(UserStatus.VERIFIED)
            .withPassword(passwordEncoder.encode(RAW_PASSWORD))
            .build();
        customer.registerMerchant("Amazon.es", "https://amazon.es");
        userRepository.save(customer);

        paymentIntent = PaymentIntent.create(
            customer.getMerchant(),
            "order_1234",
            BigDecimal.valueOf(100, 2),
            BankingAccountCurrency.EUR,
            "Amazon prime subscription"
        );

        paymentIntentRepository.save(paymentIntent);
    }

    @Test
    @DisplayName("should return checkout view")
    void getPaymentsCheckout_WhenPaymentExists_ReturnsCheckoutView() throws Exception {
        mockMvc.perform(get("/payments/{id}/checkout", paymentIntent.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("layout/main"))
            .andExpect(model().attribute("paymentId", paymentIntent.getId()))
            .andExpect(model().attribute("amount", paymentIntent.getAmount()))
            .andExpect(model().attribute("status", PaymentIntentStatus.PENDING));
    }

    @Test
    @DisplayName("should return authorized view when payment is authorized")
    void submitPaymentCheckout_WhenPaymentAuthorized_ReturnsAuthorizedView() throws Exception {
        when(paymentNetworkGateway.authorizePayment(
            any(PaymentAuthorizationRequest.class)
        )).thenReturn(new PaymentAuthorizationResponse(
            PaymentAuthorizationStatus.AUTHORIZED,
            "authorizationId",
            null
        ));

        mockMvc.perform(post("/payments/checkout")
                .param("paymentId", paymentIntent.getId().toString())
                .param("cardHolder", "John doe")
                .param("cardNumber", "1234123412341234")
                .param("cvv", "123")
                .param("expiryYear", "2029")
                .param("expiryMonth", "12")
                .param("cardPin", "1234"))
            .andExpect(status().is(302));
    }
}