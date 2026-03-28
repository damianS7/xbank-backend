package com.damian.xBank.modules.setting.application.usecase;

import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettings;
import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsQuery;
import com.damian.xBank.modules.setting.application.usecase.get.GetCurrentUserSettingsResult;
import com.damian.xBank.modules.setting.domain.model.Setting;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetCurrentUserSettingsTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetCurrentUserSettings getCurrentUserSettings;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should return current user settings")
    void getSetting_ValidRequest_ReturnsSettings() {
        // given
        setUpContext(customer);

        Setting currentUserSettings = customer.getSettings();

        GetCurrentUserSettingsQuery query = new GetCurrentUserSettingsQuery();

        // when
        when(userRepository.findById(customer.getId()))
            .thenReturn(Optional.of(customer));

        GetCurrentUserSettingsResult result = getCurrentUserSettings.execute(query);

        // then
        assertThat(result)
            .isNotNull()
            .extracting(
                r -> r.settings().language(),
                r -> r.settings().emailNotifications()
            )
            .containsExactly(
                currentUserSettings.getSettings().language(),
                currentUserSettings.getSettings().emailNotifications()
            );
        verify(userRepository, times(1)).findById(customer.getId());
    }
}
