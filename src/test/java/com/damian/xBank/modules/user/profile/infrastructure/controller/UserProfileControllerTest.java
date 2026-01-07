package com.damian.xBank.modules.user.profile.infrastructure.controller;

import com.damian.xBank.modules.user.profile.application.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDetailDto;
import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDto;
import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
        UserProfile profile = UserProfileFactory.testProfile();

        customer = UserTestBuilder
                .aCustomer()
                .withEmail("customer@demo.com")
                .withRole(UserRole.CUSTOMER)
                .withStatus(UserStatus.VERIFIED)
                .withPassword(RAW_PASSWORD)
                .withProfile(profile)
                .build();

        userRepository.save(customer);
    }

    @Test
    @DisplayName("should return current user profile")
    void getProfile_WhenValidRequest_Returns200Ok() throws Exception {
        // given
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
        UserProfileDetailDto customerWithProfileDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserProfileDetailDto.class
        );

        // then
        assertThat(customerWithProfileDTO).isNotNull();
        assertThat(customerWithProfileDTO.email()).isEqualTo(customer.getEmail());
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
        UserProfileDto customerDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                UserProfileDto.class
        );

        assertThat(customerDto)
                .isNotNull()
                .extracting(
                        UserProfileDto::firstName,
                        UserProfileDto::lastName,
                        UserProfileDto::phone,
                        UserProfileDto::birthdate,
                        UserProfileDto::gender
                ).containsExactly(
                        givenRequest.fieldsToUpdate().get("firstName"),
                        givenRequest.fieldsToUpdate().get("lastName"),
                        givenRequest.fieldsToUpdate().get("phoneNumber"),
                        givenRequest.fieldsToUpdate().get("birthdate"),
                        givenRequest.fieldsToUpdate().get("gender")
                );
    }

}
