package com.damian.xBank.modules.user.user.infrastructure.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetSetRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserPasswordControllerTest extends AbstractControllerTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create()
                   .setEmail("user@demo.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.ADMIN)
                   .setStatus(UserStatus.VERIFIED);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should update password")
    void shouldUpdatePassword() throws Exception {
        // given
        login(user);

        UserPasswordUpdateRequest updatePasswordRequest = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                "12345678$Xa"
        );

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                              .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(updatePasswordRequest)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should update password")
    void shouldNotUpdatePasswordWhenPasswordMismatch() throws Exception {
        // given
        login(user);
        UserPasswordUpdateRequest updatePasswordRequest = new UserPasswordUpdateRequest(
                "1234564",
                "12345678$Xa"
        );

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                              .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(updatePasswordRequest)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not update password when password policy not satisfied")
    void shouldNotUpdatePasswordWhenPasswordPolicyNotSatisfied() throws Exception {
        // given
        login(user);
        UserPasswordUpdateRequest updatePasswordRequest = new UserPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                               .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(JsonHelper.toJson(updatePasswordRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(
                        ApiResponse::getMessage
                ).isEqualTo(
                        ErrorCodes.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("newPassword"))
                .containsIgnoringCase("password must be at least");


    }

    @Test
    @DisplayName("Should not update password when password is null")
    void shouldNotUpdatePasswordWhenPasswordIsNull() throws Exception {
        // given
        login(user);
        UserPasswordUpdateRequest updatePasswordRequest = new UserPasswordUpdateRequest(
                "1234564",
                null
        );

        // when
        // then
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                               .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(JsonHelper.toJson(updatePasswordRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(
                        ApiResponse::getMessage
                ).isEqualTo(
                        ErrorCodes.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("newPassword"))
                .containsIgnoringCase("must not be blank");
    }

    @Test
    @DisplayName("Should send reset password token to user email")
    void shouldSendResetPasswordToken() throws Exception {
        // given
        UserPasswordResetRequest request = new UserPasswordResetRequest(
                user.getEmail()
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/password/reset")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

    @Test
    @DisplayName("Should reset password using token")
    void shouldResetPasswordUsingToken() throws Exception {
        // given
        User unverifiedUser = User.create()
                                  .setEmail("non-verified-user@demo.com")
                                  .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                  .setRole(UserRole.CUSTOMER)
                                  .setStatus(UserStatus.PENDING_VERIFICATION);
        userRepository.save(unverifiedUser);

        UserToken givenToken = UserToken.create()
                                        .setUser(unverifiedUser);
        userTokenRepository.save(givenToken);

        UserPasswordResetSetRequest request = new UserPasswordResetSetRequest(
                "12345678$Xa"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/password/reset/{token}", givenToken.getToken())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

}
