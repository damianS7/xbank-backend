package com.damian.xBank.modules.user.token.infrastructure.rest.controller;

import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.rest.request.RequestAccountVerificationRequest;
import com.damian.xBank.modules.user.token.infrastructure.rest.request.RequestPasswordResetRequest;
import com.damian.xBank.modules.user.token.infrastructure.rest.request.ResetPasswordRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTokenControllerTest extends AbstractControllerTest {
    @Autowired
    private UserTokenFactory userTokenFactory;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder
            .builder()
            .withEmail("user@demo.com")
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .withPassword(RAW_PASSWORD)
            .build();

        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should verify account when token is valid")
    void getVerifyAccount_WhenValidRequest_Returns200Ok() throws Exception {
        // given
        User unverifiedUser = UserTestBuilder.builder()
            .withEmail("non-verified-user@demo.com")
            .withPassword(passwordEncoder.encode(this.RAW_PASSWORD))
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withRole(UserRole.CUSTOMER)
            .build();

        userRepository.save(unverifiedUser);

        UserToken givenToken = userTokenFactory.verificationToken(unverifiedUser);
        userTokenRepository.save(givenToken);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/verification/{token}", givenToken.getToken())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("should resend verification token to user email")
    void postVerification_WhenValidRequest_Returns200Ok() throws Exception {
        // given
        User unverifiedUser = UserTestBuilder.builder()
            .withEmail("non-verified-user@demo.com")
            .withPassword(passwordEncoder.encode(this.RAW_PASSWORD))
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withRole(UserRole.CUSTOMER)
            .build();

        userRepository.save(unverifiedUser);

        RequestAccountVerificationRequest request = new RequestAccountVerificationRequest(
            unverifiedUser.getEmail()
        );

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/verification/resend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonHelper.toJson(request)))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("should send reset password token to user email")
    void postPasswordReset_WhenValidRequest_Returns200Ok() throws Exception {
        // given
        RequestPasswordResetRequest request = new RequestPasswordResetRequest(
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
    @DisplayName("should reset password using token")
    void postPasswordResetToken_WhenValidRequest_Returns200Ok() throws Exception {
        // given
        User unverifiedUser = UserTestBuilder.builder()
            .withEmail("non-verified-user@demo.com")
            .withPassword(passwordEncoder.encode(this.RAW_PASSWORD))
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withRole(UserRole.CUSTOMER)
            .build();
        userRepository.save(unverifiedUser);

        UserToken givenToken = userTokenFactory.passwordToken(unverifiedUser);
        userTokenRepository.save(givenToken);

        ResetPasswordRequest request = new ResetPasswordRequest(
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
