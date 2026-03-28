package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettings;
import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettingsCommand;
import com.damian.xBank.modules.setting.application.usecase.update.UpdateCurrentUserSettingsResult;
import com.damian.xBank.modules.setting.domain.model.SettingLanguage;
import com.damian.xBank.modules.setting.domain.model.SettingMultifactor;
import com.damian.xBank.modules.setting.domain.model.SettingTheme;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCurrentUserSettingsTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateCurrentUserSettings updateCurrentUserSettings;

    private User customerA;
    private User customerB;

    @BeforeEach
    void setUp() {
        customerA = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
        customerB = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should return updated settings")
    void updateSettings_WhenValidRequest_ReturnsUpdatedSettings() {
        // given
        setUpContext(customerA);

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

        UpdateCurrentUserSettingsCommand request = new UpdateCurrentUserSettingsCommand(
            newUserSettings
        );

        // when
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(customerA));

        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCurrentUserSettingsResult result = updateCurrentUserSettings.execute(request);

        // then
        assertThat(result)
            .isNotNull()
            .extracting(
                r -> r.settings().language(),
                r -> r.settings().emailNotifications()
            )
            .containsExactly(
                newUserSettings.language(),
                newUserSettings.emailNotifications()
            );
        verify(userRepository, times(1)).findById(customerA.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
