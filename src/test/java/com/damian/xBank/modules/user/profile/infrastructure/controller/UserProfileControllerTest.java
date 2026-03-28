package com.damian.xBank.modules.user.profile.infrastructure.controller;

import com.damian.xBank.modules.user.profile.application.usecase.get.GetUserProfileResult;
import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateUserProfileResult;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.infrastructure.rest.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.test.AbstractControllerTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileControllerTest extends AbstractControllerTest {
    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer().build();
        userRepository.save(customer);
    }

    @Test
    @DisplayName("should return current user profile")
    void getProfile_WhenValidRequest_Returns200Ok() throws Exception {
        // given
        String firstName = "David";
        customer.getProfile().setFirstName(firstName);
        userRepository.save(customer);

        login(customer);

        // when
        MvcResult result = mockMvc
            .perform(
                get("/api/v1/profiles")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        // then
        GetUserProfileResult response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            GetUserProfileResult.class
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.firstName())
            .isEqualTo(firstName);
    }

    @Test
    @DisplayName("Should updated user profile")
    void updateProfile_WhenValidRequest_ReturnsUpdatedUserProfile() throws Exception {
        // given
        login(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");
        fields.put("lastName", "white");
        fields.put("phoneNumber", "999 999 999");
        fields.put("birthdate", LocalDate.of(1989, 1, 1));
        fields.put("gender", UserGender.FEMALE);

        UserProfileUpdateRequest givenRequest = new UserProfileUpdateRequest(
            RAW_PASSWORD,
            fields
        );

        // when
        MvcResult result = mockMvc
            .perform(
                patch("/api/v1/profiles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .content(JsonHelper.toJson(givenRequest)))
            .andDo(print())
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        // then
        UpdateUserProfileResult response = JsonHelper.fromJson(
            result.getResponse().getContentAsString(),
            UpdateUserProfileResult.class
        );

        assertThat(response)
            .isNotNull()
            .extracting(
                UpdateUserProfileResult::firstName,
                UpdateUserProfileResult::lastName,
                UpdateUserProfileResult::phone,
                UpdateUserProfileResult::birthdate,
                UpdateUserProfileResult::gender
            ).containsExactly(
                givenRequest.fieldsToUpdate().get("firstName"),
                givenRequest.fieldsToUpdate().get("lastName"),
                givenRequest.fieldsToUpdate().get("phoneNumber"),
                givenRequest.fieldsToUpdate().get("birthdate"),
                givenRequest.fieldsToUpdate().get("gender")
            );
    }

}
