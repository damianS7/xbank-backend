package com.damian.xBank.modules.setting;

import com.damian.xBank.modules.setting.dto.request.SettingsPatchRequest;
import com.damian.xBank.modules.setting.dto.response.SettingDto;
import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.enums.CustomerGender;
import com.damian.xBank.shared.AbstractIntegrationTest;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.Setting;
import com.damian.xBank.shared.utils.JsonHelper;
import org.junit.jupiter.api.*;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SettingIntegrationTest extends AbstractIntegrationTest {

    private Customer customer;

    @BeforeAll
    void setUp() {
        customer = Customer.create()
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                           .setFirstName("David")
                           .setLastName("Brow")
                           .setBirthdate(LocalDate.now())
                           .setPhotoPath("avatar.jpg")
                           .setPhone("123 123 123")
                           .setPostalCode("01003")
                           .setAddress("Fake ave")
                           .setCountry("US")
                           .setGender(CustomerGender.MALE);
        customer.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customer);
    }

    @AfterEach
    void tearDown() {
        settingRepository.deleteAll();
    }

    @Test
    @DisplayName("Should get logged user settings")
    void shouldGetSettings() throws Exception {
        // given
        login(customer);

        Map<String, Object> givenSettings = new HashMap<>();
        givenSettings.put("lang", "en");
        givenSettings.put("2fa", "enabled");

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
        assertThat(settings).isNotNull();
    }

    @Test
    @DisplayName("Should update customer settings")
    void shouldUpdateSettings() throws Exception {
        // given
        login(customer);

        Map<String, Object> givenSettings = new HashMap<>();
        givenSettings.put("lang", "en");

        Setting givenSetting = Setting.create(customer)
                                      .setSettings(givenSettings);
        settingRepository.save(givenSetting);

        SettingsPatchRequest request = new SettingsPatchRequest(
                Map.of("lang", "es")
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
                        r -> r.settings().get("lang")
                )
                .isEqualTo("es");
    }
}