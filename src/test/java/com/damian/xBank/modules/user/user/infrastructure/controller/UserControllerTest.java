package com.damian.xBank.modules.user.user.infrastructure.controller;

import com.damian.xBank.modules.user.user.application.dto.request.UserAccountEmailUpdateRequest;
import com.damian.xBank.modules.user.user.application.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest extends AbstractControllerTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create()
                   .setEmail("user@demo.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.ADMIN);

        user.setStatus(UserStatus.VERIFIED);
        userAccountRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userAccountTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() throws Exception {
        // given
        login(user);

        UserAccountEmailUpdateRequest givenRequest = new UserAccountEmailUpdateRequest(
                RAW_PASSWORD,
                "user2@test.com"
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/accounts/email")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonHelper.toJson(givenRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn();

        // then
        UserAccountDto userAccountDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                UserAccountDto.class
        );

        // then
        assertThat(userAccountDto)
                .isNotNull()
                .extracting(
                        UserAccountDto::id,
                        UserAccountDto::email
                ).containsExactly(
                        user.getId(),
                        givenRequest.newEmail()
                );
    }
}
