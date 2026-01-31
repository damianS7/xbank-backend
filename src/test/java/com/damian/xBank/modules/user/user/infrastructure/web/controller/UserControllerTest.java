package com.damian.xBank.modules.user.user.infrastructure.web.controller;

import com.damian.xBank.modules.user.user.application.dto.request.UserEmailUpdateRequest;
import com.damian.xBank.modules.user.user.application.dto.response.UserDto;
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
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() throws Exception {
        // given
        login(user);

        UserEmailUpdateRequest givenRequest = new UserEmailUpdateRequest(
                RAW_PASSWORD,
                "user2@test.com"
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/users/email")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonHelper.toJson(givenRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn();

        // then
        UserDto userDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                UserDto.class
        );

        // then
        assertThat(userDto)
                .isNotNull()
                .extracting(
                        UserDto::id,
                        UserDto::email
                ).containsExactly(
                        user.getId(),
                        givenRequest.newEmail()
                );
    }
}
