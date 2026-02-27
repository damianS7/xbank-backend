package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.cqrs.result.SettingResult;
import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingGetTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingGet settingGet;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.customer();
    }

    @Test
    @DisplayName("should return current user settings")
    void getSetting_ValidRequest_ReturnsSettings() {
        // given
        setUpContext(customer);

        UserSettings givenUserSettings = UserSettings.defaults();

        Setting givenSettings = Setting.create(customer)
            .setSettings(givenUserSettings);

        // when
        when(settingRepository.findByUser_Id(customer.getId()))
            .thenReturn(Optional.of(givenSettings));

        SettingResult result = settingGet.execute();

        // then
        assertThat(result)
            .isNotNull()
            .extracting(
                r -> r.settings().language(),
                r -> r.settings().emailNotifications()
            )
            .containsExactly(
                givenUserSettings.language(),
                givenUserSettings.emailNotifications()
            );
        verify(settingRepository, times(1)).findByUser_Id(customer.getId());
    }
}
