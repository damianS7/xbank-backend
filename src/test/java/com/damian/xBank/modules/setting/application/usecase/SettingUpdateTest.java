package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.dto.request.SettingsUpdateRequest;
import com.damian.xBank.modules.setting.domain.model.*;
import com.damian.xBank.modules.setting.domain.service.SettingService;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SettingUpdateTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @Spy
    private SettingService settingService;

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
    @DisplayName("execute should update settings")
    void execute_Valid_ReturnsUpdatedSettings() {
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
}
