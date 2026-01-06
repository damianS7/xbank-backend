package com.damian.xBank.modules.user.token.infrastructure.web.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenRequestPasswordResetRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserTokenResetPasswordRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserTokenVerificationRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.model.UserTokenType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTokenControllerTest extends AbstractControllerTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder
                .aCustomer()
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
        User unverifiedUser = User.create()
                                  .setEmail("non-verified-user@demo.com")
                                  .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                  .setStatus(UserStatus.PENDING_VERIFICATION);

        userRepository.save(unverifiedUser);

        UserToken givenToken = UserToken.create()
                                        .setType(UserTokenType.ACCOUNT_VERIFICATION)
                                        .setUser(unverifiedUser);

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
        User unverifiedUser = User.create()
                                  .setEmail("non-verified-user@demo.com")
                                  .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                  .setStatus(UserStatus.PENDING_VERIFICATION);

        userRepository.save(unverifiedUser);

        UserTokenVerificationRequest request = new UserTokenVerificationRequest(
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
        UserTokenRequestPasswordResetRequest request = new UserTokenRequestPasswordResetRequest(
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
        User unverifiedUser = User.create()
                                  .setEmail("non-verified-user@demo.com")
                                  .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                  .setRole(UserRole.CUSTOMER)
                                  .setStatus(UserStatus.PENDING_VERIFICATION);
        userRepository.save(unverifiedUser);

        UserToken givenToken = UserToken.create()
                                        .setUser(unverifiedUser);
        userTokenRepository.save(givenToken);

        UserTokenResetPasswordRequest request = new UserTokenResetPasswordRequest(
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
