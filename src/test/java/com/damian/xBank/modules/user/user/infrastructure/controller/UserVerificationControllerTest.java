package com.damian.xBank.modules.user.user.infrastructure.controller;

import com.damian.xBank.modules.user.token.application.dto.request.UserPasswordResetSetRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.model.UserTokenType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserVerificationControllerTest extends AbstractControllerTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create()
                   .setEmail("user@demo.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.ADMIN);

        user.setStatus(UserStatus.VERIFIED);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should verify account using token")
    void shouldVerifyAccountUsingToken() throws Exception {
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

        UserPasswordResetSetRequest request = new UserPasswordResetSetRequest(
                "12345678$Xa"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/verification/{token}", givenToken.getToken())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

}
