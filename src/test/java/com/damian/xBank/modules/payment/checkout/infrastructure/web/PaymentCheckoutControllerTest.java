package com.damian.xBank.modules.payment.checkout.infrastructure.web;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PaymentCheckoutControllerTest extends AbstractControllerTest {
    @MockitoBean
    private PaymentNetworkGateway paymentNetworkGateway;

    private User customer;
    private PaymentIntent paymentIntent;


    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withEmail("customer@demo.com")
                                  .withStatus(UserStatus.VERIFIED)
                                  .withPassword(passwordEncoder.encode(RAW_PASSWORD))
                                  .build();

        userRepository.save(customer);

        paymentIntent = new PaymentIntent(
                customer,
                BigDecimal.valueOf(100, 2),
                BankingAccountCurrency.EUR
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
               .andExpect(model().attribute("status", paymentIntent.getStatus().toString()));
    }

    @Test
    @DisplayName("should return authorized view when payment is authorized")
    void postPaymentsCheckout_WhenPaymentAuthorized_ReturnsAuthorizedView() throws Exception {
        when(paymentNetworkGateway.authorizePayment(
                anyString(),
                anyString(),
                anyString(),
                anyInt(),
                anyInt(),
                any(BigDecimal.class),
                anyString()
        )).thenReturn(new PaymentAuthorizationResponse(
                PaymentAuthorizationStatus.AUTHORIZED,
                "authorizationId",
                null
        ));

        mockMvc.perform(post("/payments/{id}/checkout", paymentIntent.getId())
                       .param("paymentId", paymentIntent.getId().toString())
                       .param("cardNumber", "1234123412341234")
                       .param("cvv", "123")
                       .param("cardPin", "1234"))
               .andExpect(status().isOk())
               .andExpect(view().name("layout/main"))
               .andExpect(model().attribute("status", PaymentAuthorizationStatus.AUTHORIZED.toString()));
    }
}