package com.damian.xBank.modules.setting.domain;

import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.domain.model.*;
import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class SettingDomainServiceTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingDomainService settingDomainService;

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
    @DisplayName("updateSettings should update settings")
    void updateSettings_Valid_ReturnsUpdatedSettings() {
        // given
        UserSettings settingsValues = new UserSettings(
                true,
                true,
                false,
                false,
                "",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.EN,
                SettingTheme.LIGHT
        );

        Setting customerSettings = Setting.create(customerA)
                                          .setSettings(settingsValues);

        UserSettings newSettings = new UserSettings(
                true,
                true,
                false,
                false,
                "",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.ES,
                SettingTheme.LIGHT
        );

        // when
        settingDomainService.updateSettings(
                customerA.getId(),
                customerSettings,
                newSettings
        );

        // then
        assertThat(customerSettings)
                .isNotNull()
                .extracting(
                        r -> r.getSettings().language(),
                        r -> r.getSettings().emailNotifications()
                )
                .containsExactly(
                        newSettings.language(),
                        newSettings.emailNotifications()
                );
    }

    @Test
    @DisplayName("updateSettings should update settings")
    void updateSettings_NotOwner_ThrowsException() {
        // given
        UserSettings settingsValues = new UserSettings(
                true,
                true,
                false,
                false,
                "",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.EN,
                SettingTheme.LIGHT
        );

        Setting customerSettings = Setting.create(customerA)
                                          .setSettings(settingsValues);

        UserSettings newSettings = new UserSettings(
                true,
                true,
                false,
                false,
                "",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.ES,
                SettingTheme.LIGHT
        );

        // when
        SettingNotOwnerException exception =
                assertThrows(
                        SettingNotOwnerException.class,
                        () -> settingDomainService.updateSettings(
                                customerB.getId(),
                                customerSettings,
                                newSettings
                        )
                );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.SETTING_NOT_OWNER);
    }

    @Test
    @DisplayName("initializeDefaultSettingsFor should return settings with default values")
    void initializeDefaultSettingsFor_ValidUserAccount_ReturnsSettingWithDefaultValues() {
        // given
        // when
        Setting result = settingDomainService.initializeDefaultSettingsFor(customerA.getAccount());

        // then
        assertThat(result).isNotNull();

        assertThat(result.getUserAccount())
                .isSameAs(customerA.getAccount());

        assertThat(result.getSettings())
                .isEqualTo(UserSettings.defaults());
    }
}
