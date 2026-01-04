package com.damian.xBank.modules.user.token.infrastructure.web.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetRequest;
import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetSetRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.user.application.dto.request.UserPasswordUpdateRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTokenPasswordControllerTest extends AbstractControllerTest {
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
