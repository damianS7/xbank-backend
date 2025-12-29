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

    private Customer customerA;
    private Customer customerB;

    @BeforeEach
    void setUp() {
        customerA = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customerA@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerB = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerB@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);
    }

    @Test
    @DisplayName("execute a valid request should get current user settings")
    void execute_ValidRequest_ReturnsSettings() {
        // given
        setUpContext(customerA.getAccount());

        UserSettings givenUserSettings = UserSettings.defaults();

        Setting givenSettings = Setting.create(customerA)
                                       .setSettings(givenUserSettings);

        // when
        when(settingRepository.findByUser_Id(customerA.getId()))
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
        verify(settingRepository, times(1)).findByUser_Id(customerA.getId());
    }
}
