package com.damian.xBank.modules.user.account;

import com.damian.xBank.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.xBank.modules.user.account.account.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.shared.AbstractIntegrationTest;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.ApiResponse;
import com.damian.xBank.shared.utils.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountRegistrationIntegrationTest extends AbstractIntegrationTest {
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.create()
                                 .setEmail("user@demo.com")
                                 .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                 .setRole(UserAccountRole.ADMIN)
                                 .setAccountStatus(UserAccountStatus.VERIFIED);
        userAccountRepository.save(userAccount);
    }

    @AfterEach
    void tearDown() {
        userAccountTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create account when request is valid")
    void shouldRegisterAccountWhenValidRequest() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@gmail.com",
                "12345678X$"
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/accounts/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
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
                        UserAccountDto::email
                ).isEqualTo(
                        request.email()
                );
    }

    @Test
    @DisplayName("Should not register user when missing fields")
    void shouldNotRegisterAccountWhenMissingFields() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@test.com",
                "123456"
        );

        // then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/register")
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
                .isEqualTo(Exceptions.COMMON.VALIDATION_FAILED);
    }

    @Test
    @DisplayName("Should not register user when email is not well-formed")
    void shouldNotRegisterAccountWhenEmailIsNotWellFormed() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "badEmail",
                "1234567899X$"
        );

        // then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/register")
                                                                 .contentType(MediaType.APPLICATION_JSON)
                                                                 .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  //                                  .andExpect(jsonPath("$.errors.email").value(containsString(
                                  //                                          "Email must be a well-formed email address")))
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
                        Exceptions.COMMON.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("email"))
                .contains("must be a well-formed email address");
    }

    @Test
    @DisplayName("Should not register user when email is taken")
    void shouldNotRegisterAccountWhenEmailIsTaken() throws Exception {
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                userAccount.getEmail(),
                "12345678X$"
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/accounts/register")
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
                .extracting(ApiResponse::getMessage)
                .asString()
                .isEqualTo(Exceptions.USER.ACCOUNT.EMAIL_TAKEN);
    }

    @Test
    @DisplayName("Should not register user when password policy not satisfied")
    void shouldNotRegisterAccountWhenPasswordPolicyNotSatisfied() throws Exception {
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "user@demo.com",
                "123456"
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/accounts/register")
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
                        Exceptions.COMMON.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("password"))
                .containsIgnoringCase("password must be at least");
    }
}
