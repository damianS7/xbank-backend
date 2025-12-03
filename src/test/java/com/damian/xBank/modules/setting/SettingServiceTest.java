package com.damian.xBank.modules.setting;

import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.application.service.SettingService;
import com.damian.xBank.modules.setting.domain.entity.Setting;
import com.damian.xBank.modules.setting.domain.enums.SettingLanguage;
import com.damian.xBank.modules.setting.domain.enums.SettingMultifactor;
import com.damian.xBank.modules.setting.domain.enums.SettingTheme;
import com.damian.xBank.modules.setting.infra.repository.SettingRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SettingServiceTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingService settingService;

    @Test
    @DisplayName("Should get settings for the current user")
    void shouldGetSettings() {
        // given
        User currentUser = User.create()
                               .setId(1L)
                               .setEmail("user@demo.com");

        setUpContext(currentUser);

        UserSettings givenUserSettings = UserSettings.defaults();

        Setting givenSettings = Setting.create(currentUser)
                                       .setSettings(givenUserSettings);

        // when
        when(settingRepository.findByUser_Id(currentUser.getId()))
                .thenReturn(Optional.of(givenSettings));
        Setting result = settingService.getSettings();

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
        verify(settingRepository, times(1)).findByUser_Id(currentUser.getId());
    }

    @Test
    @DisplayName("Should update settings")
    void shouldUpdateSettings() {
        // given
        User currentUser = User.create()
                               .setId(1L)
                               .setEmail("user@demo.com");
        setUpContext(currentUser);

        UserSettings userSettings = new UserSettings(
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

        Setting givenSettings = Setting.create(currentUser)
                                       .setSettings(userSettings);

        UserSettings updatedUserSettings = new UserSettings(
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
                updatedUserSettings
        );

        // when
        when(settingRepository.findByUser_Id(currentUser.getId()))
                .thenReturn(Optional.of(givenSettings));
        when(settingRepository.save(any(Setting.class))).thenReturn(givenSettings);
        Setting result = settingService.updateSettings(request);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        r -> r.getSettings().language(),
                        r -> r.getSettings().emailNotifications()
                )
                .containsExactly(
                        updatedUserSettings.language(),
                        updatedUserSettings.emailNotifications()
                );
        verify(settingRepository, times(1)).findByUser_Id(currentUser.getId());
        verify(settingRepository, times(1)).save(any(Setting.class));
    }
}
