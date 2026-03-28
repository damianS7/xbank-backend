package com.damian.xBank.modules.payment.intent.infrastructure.rest.controller;

import com.damian.xBank.modules.payment.intent.infrastructure.rest.request.CreatePaymentIntentRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentIntentControllerTest extends AbstractControllerTest {
    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aMerchant();
        userRepository.save(customer);
    }

    @Test
    @DisplayName("should return created payment intent")
    void postPaymentIntent_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(customer);

        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
            "order_1234",
            "Amazon prime subscription",
            BigDecimal.valueOf(100),
            "EUR"
        );

        // when
        // then
        mockMvc.perform(post("/api/v1/payment-intents")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is(201));
    }
}