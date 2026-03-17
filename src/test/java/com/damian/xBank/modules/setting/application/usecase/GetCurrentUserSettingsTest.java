package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettings;
import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsQuery;
import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsResult;
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

public class GetCurrentUserSettingsTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private GetCurrentUserSettings getCurrentUserSettings;

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

        Setting setting = Setting.create(customer, UserSettings.defaults());

        GetCurrentUserSettingsQuery query = new GetCurrentUserSettingsQuery();

        // when
        when(settingRepository.findByUser_Id(customer.getId()))
            .thenReturn(Optional.of(setting));

        GetCurrentUserSettingsResult result = getCurrentUserSettings.execute(query);

        // then
        assertThat(result)
            .isNotNull()
            .extracting(
                r -> r.settings().language(),
                r -> r.settings().emailNotifications()
            )
            .containsExactly(
                setting.getSettings().language(),
                setting.getSettings().emailNotifications()
            );
        verify(settingRepository, times(1)).findByUser_Id(customer.getId());
    }
}
