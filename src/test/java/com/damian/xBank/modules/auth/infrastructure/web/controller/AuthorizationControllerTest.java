package com.damian.xBank.modules.auth.infrastructure.web.controller;

import com.damian.xBank.modules.user.profile.application.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.JwtUtil;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorizationControllerTest extends AbstractControllerTest {

    @Autowired
    private JwtUtil jwtUtil;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder
                .aCustomer()
                .withEmail("customer@demo.com")
                .withRole(UserRole.CUSTOMER)
                .withStatus(UserStatus.VERIFIED)
                .withPassword(passwordEncoder.encode(RAW_PASSWORD))
                .build();

        userRepository.save(customer);
    }

    @Test
    @DisplayName("GET /test returns 200 OK when JWT token is valid")
    void getCustomers_WithValidToken_Returns200OK() throws Exception {
        // given
        final String givenToken = jwtUtil.generateToken(
                customer.getEmail(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/test")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("GET /test returns 401 Unauthorized when request is not authenticated")
    void getCustomers_WithoutAuthentication_Returns401Unauthorized() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/test"))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /test returns 401 Unauthorized when JWT token is expired")
    void getCustomers_WithExpiredToken_Returns401Unauthorized() throws Exception {
        // given
        final String expiredToken = jwtUtil.generateToken(
                customer.getEmail(),
                new Date(System.currentTimeMillis() - 1000 * 60 * 60)
        );

        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/test")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                       .content(jsonRequest))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(ErrorCodes.AUTH_JWT_TOKEN_EXPIRED))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /test returns 401 Unauthorized when JWT token is invalid")
    void getCustomers_WithInvalidToken_Returns401Unauthorized() throws Exception {
        // given
        final String invalidToken = "bad-token";

        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/test")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                       .content(jsonRequest))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(ErrorCodes.AUTH_JWT_TOKEN_INVALID))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /test returns 401 Unauthorized when JWT token email does not exist")
    void getCustomers_WithNonExistingEmailInToken_Returns401Unauthorized() throws Exception {
        // given
        final String token = jwtUtil.generateToken(
                "fake-email@demo.com",
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/test")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
