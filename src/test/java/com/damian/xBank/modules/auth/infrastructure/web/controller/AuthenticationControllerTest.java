package com.damian.xBank.modules.auth.infrastructure.web.controller;

import com.damian.xBank.modules.auth.application.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.application.dto.AuthenticationResponse;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;
import com.damian.xBank.modules.user.user.domain.model.UserAccountStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.shared.utils.UserTestBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest extends AbstractControllerTest {
    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder
                .aCustomer()
                .withEmail("customer@demo.com")
                .withRole(UserAccountRole.CUSTOMER)
                .withStatus(UserAccountStatus.VERIFIED)
                .withPassword(RAW_PASSWORD)
                .build();

        userAccountRepository.save(customer);
    }

    @Test
    @DisplayName("POST /auth/login returns 200 OK when credentials are valid")
    void login_WithValidCredentials_Returns200OK() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // json to AuthenticationResponse
        AuthenticationResponse response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // then
        assertThat(response)
                .isNotNull();
        assertThat(jwtUtil.extractEmail(response.token())).isEqualTo(customer.getEmail());
        assertTrue(jwtUtil.isTokenValid(response.token()));
    }

    @Test
    @DisplayName("POST /auth/login returns 401 Unauthorized when credentials are invalid")
    void login_WithInvalidCredentials_Returns401Unauthorized() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                "badPassword"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .post("/api/v1/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /auth/login returns 401 Unauthorized when email does not exist")
    void login_WithNonExistingEmail_Returns401Unauthorized() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                "nonemail@demo.com",
                "123456"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getErrorCode)
                .isEqualTo(
                        ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS
                );
    }

    @Test
    @DisplayName("POST /auth/login returns 403 Forbidden when account is suspended")
    void login_WhenAccountSuspended_Returns403Forbidden() throws Exception {
        // given
        customer.setAccountStatus(UserAccountStatus.SUSPENDED);
        userAccountRepository.save(customer);

        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getErrorCode)
                .isEqualTo(
                        ErrorCodes.USER_ACCOUNT_SUSPENDED
                );

        // undo changes
        customer.setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(customer);
    }

    @Test
    @DisplayName("POST /auth/login returns 403 Forbidden when account is disabled")
    void login_WhenAccountDisabled_Returns403Forbidden() throws Exception {
        // given
        customer.setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);
        userAccountRepository.save(customer);

        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getErrorCode)
                .isEqualTo(
                        ErrorCodes.USER_ACCOUNT_NOT_VERIFIED
                );

        // undo changes to customer
        customer.setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(customer);
    }

    @Test
    @DisplayName("POST /auth/login returns 400 Bad Request when email format is invalid")
    void login_WithInvalidEmailFormat_Returns400BadRequest() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                "thisIsNotAnEmail",
                "123456"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getMessage)
                .isEqualTo(
                        ErrorCodes.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("email"))
                .asString()
                .contains("must be a well-formed email address");
    }

    @Test
    @DisplayName("POST /auth/login returns 400 Bad Request when required fields are null")
    void login_WithNullFields_Returns400BadRequest() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(
                null,
                null
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getMessage)
                .isEqualTo(
                        ErrorCodes.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("password"))
                .asString()
                .contains("must not be blank");

        assertThat(response.getErrors().get("email"))
                .asString()
                .contains("must not be blank");
    }
}