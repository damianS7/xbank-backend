package com.damian.xBank.modules.auth.infrastructure.controller;

import com.damian.xBank.modules.auth.application.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.application.dto.AuthenticationResponse;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.JsonHelper;
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
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.create()
                                 .setEmail("user@demo.com")
                                 .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                                 .setRole(UserAccountRole.CUSTOMER)
                                 .setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(userAccount);
    }

    @Test
    @DisplayName("Should login when valid credentials")
    void shouldLoginWhenValidCredentials() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
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
        assertThat(jwtUtil.extractEmail(response.token())).isEqualTo(userAccount.getEmail());
        assertTrue(jwtUtil.isTokenValid(response.token()));
    }

    @Test
    @DisplayName("Should not login when invalid credentials")
    void shouldNotLoginWhenInvalidCredentials() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
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
    @DisplayName("Should not login when email not exist")
    void shouldNotLoginWhenEmailNotExist() throws Exception {
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
    @DisplayName("Should not login when account is locked or suspended")
    void shouldNotLoginWhenAccountIsLockedOrSuspended() throws Exception {
        // given
        userAccount.setAccountStatus(UserAccountStatus.SUSPENDED);
        userAccountRepository.save(userAccount);

        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
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
        userAccount.setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(userAccount);
    }

    @Test
    @DisplayName("Should not login when invalid email format")
    void shouldNotLoginWhenInvalidEmailFormat() throws Exception {
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
    @DisplayName("Should not login when null fields")
    void shouldNotLoginWhenNullFields() throws Exception {
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

    @Test
    @DisplayName("Should not login when account is disabled or not verified")
    void shouldNotLoginWhenAccountIsDisabledOrNotVerified() throws Exception {
        // given
        userAccount.setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);
        userAccountRepository.save(userAccount);

        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
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
        userAccount.setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(userAccount);
    }
}