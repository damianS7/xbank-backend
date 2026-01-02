package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.domain.exception.SettingNotOwnerException;
import com.damian.xBank.modules.setting.domain.model.*;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class SettingUpdateTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingUpdate settingUpdate;

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
    @DisplayName("should return updated settings")
    void updateSettings_WhenValidRequest_ReturnsUpdatedSettings() {
        // given
        setUpContext(customerA.getAccount());

        UserSettings customerCurrentSettings = new UserSettings(
                true,
                false,
                false,
                false,
                "",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.EN,
                SettingTheme.LIGHT
        );

        Setting givenSettings = Setting.create(customerA)
                                       .setSettings(customerCurrentSettings);

        UserSettings newUserSettings = new UserSettings(
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

        SettingsUpdateRequest request = new SettingsUpdateRequest(
                newUserSettings
        );

        // when
        when(settingRepository.findByUser_Id(anyLong()))
                .thenReturn(Optional.of(givenSettings));

        when(settingRepository.save(any(Setting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Setting result = settingUpdate.execute(request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        r -> r.getSettings().language(),
                        r -> r.getSettings().emailNotifications()
                )
                .containsExactly(
                        newUserSettings.language(),
                        newUserSettings.emailNotifications()
                );
        verify(settingRepository, times(1)).findByUser_Id(customerA.getId());
        verify(settingRepository, times(1)).save(any(Setting.class));
    }

    @Test
    @DisplayName("should throw exception when user is not the owner of the settings")
    void updateSettings_WhenNotOwner_ThrowsException() {
        // given
        setUpContext(customerA.getAccount());

        UserSettings customerCurrentSettings = new UserSettings(
                true,
                false,
                false,
                false,
                "",
                60,
                SettingMultifactor.EMAIL,
                SettingLanguage.EN,
                SettingTheme.LIGHT
        );

        Setting givenSettings = Setting.create(customerB)
                                       .setSettings(customerCurrentSettings);

        UserSettings newUserSettings = new UserSettings(
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

        SettingsUpdateRequest request = new SettingsUpdateRequest(
                newUserSettings
        );

        // when
        when(settingRepository.findByUser_Id(anyLong()))
                .thenReturn(Optional.of(givenSettings));

        // when
        SettingNotOwnerException exception =
                assertThrows(
                        SettingNotOwnerException.class,
                        () -> settingUpdate.execute(request)
                );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.SETTING_NOT_OWNER);
    }
}
