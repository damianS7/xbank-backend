package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SettingGetTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingGet settingGet;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);
    }

    @Test
    @DisplayName("should return current user settings")
    void getSetting_ValidRequest_ReturnsSettings() {
        // given
        setUpContext(customer.getAccount());

        UserSettings givenUserSettings = UserSettings.defaults();

        Setting givenSettings = Setting.create(customer)
                                       .setSettings(givenUserSettings);

        // when
        when(settingRepository.findByUser_Id(customer.getId()))
                .thenReturn(Optional.of(givenSettings));

        Setting result = settingGet.execute();

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        r -> r.getSettings().language(),
                        r -> r.getSettings().emailNotifications()
                )
                .containsExactly(
                        givenUserSettings.language(),
                        givenUserSettings.emailNotifications()
                );
        verify(settingRepository, times(1)).findByUser_Id(customer.getId());
    }
}
