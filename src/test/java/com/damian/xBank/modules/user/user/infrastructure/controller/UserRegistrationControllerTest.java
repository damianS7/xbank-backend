package com.damian.xBank.modules.user.user.infrastructure.controller;

import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDetailDto;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountRegistrationRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.dto.ApiResponse;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class UserRegistrationControllerTest extends AbstractControllerTest {

    @Test
    @DisplayName("Should register a customer")
    void shouldRegisterCustomer() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "customer@test.com",
                "12345689X$$sa",
                "Customer",
                "Test",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/customers/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        UserProfileDetailDto customerDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                UserProfileDetailDto.class
        );

        // then
        assertThat(customerDto)
                .isNotNull()
                .extracting(
                        UserProfileDetailDto::email,
                        UserProfileDetailDto::firstName,
                        UserProfileDetailDto::lastName,
                        UserProfileDetailDto::phone,
                        UserProfileDetailDto::birthdate,
                        UserProfileDetailDto::gender
                ).containsExactly(
                        request.email(),
                        request.firstName(),
                        request.lastName(),
                        request.phoneNumber(),
                        request.birthdate(),
                        request.gender()
                );
    }

    @Test
    @DisplayName("Should not register customer when missing fields")
    void shouldNotRegisterCustomerWhenMissingFields() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "customer@test.com",
                "12345689X$$sa",
                "",
                "",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        // then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/register")
                                                                 .contentType(MediaType.APPLICATION_JSON)
                                                                 .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getMessage)
                .asString()
                .isEqualTo(ErrorCodes.VALIDATION_FAILED);
    }

    @Test
    @DisplayName("Should not register customer when email is not well-formed")
    void shouldNotRegisterCustomerWhenEmailIsNotWellFormed() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "bad-email.com",
                "12345689X$$sa",
                "Customer",
                "Test",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        // then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/register")
                                                                 .contentType(MediaType.APPLICATION_JSON)
                                                                 .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
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

        assertThat(response.getErrors().get("email"))
                .contains("must be a well-formed email address");
    }

    @Test
    @DisplayName("Should not register customer when email is taken")
    void shouldNotRegisterCustomerWhenEmailIsTaken() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "customer@test.com",
                "12345699Xxs$$",
                "Customer",
                "Test",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        User givenUser = User.create()
                             .setEmail(request.email())
                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        userAccountRepository.save(givenUser);


        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/customers/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getErrorCode)
                .asString()
                .isEqualTo(ErrorCodes.USER_ACCOUNT_EMAIL_TAKEN);

    }

    @Test
    @DisplayName("Should not register customer when password policy not satisfied")
    void shouldNotRegisterCustomerWhenPasswordPolicyNotSatisfied() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "customer@test.com",
                "123456",
                "Customer",
                "Test",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/customers/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
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

        assertThat(response.getErrors().get("password"))
                .containsIgnoringCase("password must be at least");
    }
}
