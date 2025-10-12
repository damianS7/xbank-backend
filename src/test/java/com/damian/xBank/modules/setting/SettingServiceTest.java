package com.damian.xBank.modules.setting;

import com.damian.xBank.modules.setting.dto.request.SettingsPatchRequest;
import com.damian.xBank.modules.setting.repository.SettingRepository;
import com.damian.xBank.modules.setting.service.SettingService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.Setting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SettingServiceTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingService settingService;

    @Test
    @DisplayName("Should get settings for the current customer")
    void shouldGetSettings() {
        // given
        Customer currentCustomer = Customer.create()
                                           .setId(1L)
                                           .setEmail("user@demo.com");
        setUpContext(currentCustomer);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key1", "value1");
        settings.put("key2", "value2");

        Setting givenSettings = new Setting(currentCustomer, settings);

        // when
        when(settingRepository.findByCustomer_Id(currentCustomer.getId()))
                .thenReturn(Optional.of(givenSettings));
        Setting result = settingService.getSettings();

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        r -> r.getSetting("key1"),
                        r -> r.getSetting("key2")
                )
                .containsExactly("value1", "value2");
        verify(settingRepository, times(1)).findByCustomer_Id(currentCustomer.getId());
    }

    @Test
    @DisplayName("Should update settings")
    void shouldUpdateSettings() {
        // given
        Customer currentCustomer = Customer.create()
                                           .setId(1L)
                                           .setEmail("user@demo.com");
        setUpContext(currentCustomer);

        Map<String, Object> settings = new HashMap<>();
        settings.put("key1", "value1");

        Setting givenSettings = new Setting(currentCustomer, settings);

        SettingsPatchRequest request = new SettingsPatchRequest(
                Map.of("key1", "newValue")
        );

        // when
        when(settingRepository.findByCustomer_Id(currentCustomer.getId()))
                .thenReturn(Optional.of(givenSettings));
        when(settingRepository.save(any(Setting.class))).thenReturn(givenSettings);
        Setting result = settingService.updateSettings(request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        r -> r.getSetting("key1")
                )
                .isEqualTo(request.settings().get("key1"));
        verify(settingRepository, times(2)).findByCustomer_Id(currentCustomer.getId());
        verify(settingRepository, times(1)).save(any(Setting.class));
    }
}
