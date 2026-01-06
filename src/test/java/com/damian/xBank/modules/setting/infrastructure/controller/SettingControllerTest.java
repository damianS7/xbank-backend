package com.damian.xBank.modules.setting.infrastructure.controller;

import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.application.dto.response.SettingDto;
import com.damian.xBank.modules.setting.domain.model.*;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.utils.JsonHelper;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SettingControllerTest extends AbstractControllerTest {

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder
                .aCustomer()
                .withEmail("customer@demo.com")
                .withRole(UserRole.CUSTOMER)
                .withStatus(UserStatus.VERIFIED)
                .withPassword(passwordEncoder.encode(RAW_PASSWORD))
                .build();

        userRepository.save(customer);
    }

    @AfterEach
    void tearDown() {
        settingRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /settings returns current user settings")
    void getSettings_ValidRequest_ReturnsSettingsAnd200OK() throws Exception {
        // given
        login(customer);

        UserSettings givenSettings = UserSettings.defaults();

        Setting givenSetting = Setting.create(customer)
                                      .setSettings(givenSettings);

        settingRepository.save(givenSetting);

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/settings")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        SettingDto settings = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                SettingDto.class
        );

        // then
        assertThat(settings.settings()).isNotNull();
    }

    @Test
    @DisplayName("PATCH /settings updates current user settings")
    void updateSettings_ValidRequest_ReturnsUpdatedSettingsAnd200OK() throws Exception {
        // given
        login(customer);

        UserSettings givenSettings = UserSettings.defaults();

        Setting givenSetting = Setting.create(customer)
                                      .setSettings(givenSettings);

        settingRepository.save(givenSetting);

        SettingsUpdateRequest request = new SettingsUpdateRequest(
                new UserSettings(
                        true,
                        true,
                        false,
                        false,
                        "",
                        60,
                        SettingMultifactor.EMAIL,
                        SettingLanguage.ES,
                        SettingTheme.LIGHT
                )
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/settings", JsonHelper.toJson(request))
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        SettingDto settings = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                SettingDto.class
        );

        // then
        assertThat(settings)
                .isNotNull()
                .extracting(
                        r -> r.settings().language()
                )
                .isEqualTo(request.settings().language());
    }
}