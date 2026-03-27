package com.damian.xBank.modules.user.user.infrastructure.rest.controller;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.UserEmailUpdateRequest;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.modules.user.utils.UserTestFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class UserControllerTest extends AbstractControllerTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomer();
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
    }
}
