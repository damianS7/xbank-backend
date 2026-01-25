package com.damian.xBank.modules.payment.intent.infrastructure.web.controller;

import com.damian.xBank.modules.payment.intent.application.dto.request.CreatePaymentIntentRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
        customer = UserTestBuilder.aCustomer()
                                  .withEmail("customer@demo.com")
                                  .withStatus(UserStatus.VERIFIED)
                                  .withPassword(passwordEncoder.encode(RAW_PASSWORD))
                                  .build();

        userRepository.save(customer);
    }

    @Test
    @DisplayName("should return created payment intent")
    void postPaymentIntent_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(customer);

        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                BigDecimal.valueOf(100),
                "EUR"
        );

        // when
        // then
        mockMvc.perform(post("/api/v1/payments")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andDo(print())
               .andExpect(status().is(201));
    }
}