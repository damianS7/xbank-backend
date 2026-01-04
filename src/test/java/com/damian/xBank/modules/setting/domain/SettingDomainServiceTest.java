package com.damian.xBank.modules.setting.domain;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

public class SettingDomainServiceTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingDomainService settingDomainService;

    private User customerA;
    private User customerB;

    @BeforeEach
    void setUp() {
        customerA = UserTestBuilder.aCustomer()
                                   .withId(1L)
                                   .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                   .withEmail("customerA@demo.com")
                                   .build();

        customerB = UserTestBuilder.aCustomer()
                                   .withId(2L)
                                   .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                   .withEmail("customerA@demo.com")
                                   .build();
    }

    @Test
    @DisplayName("initializeDefaultSettingsFor should return settings with default values")
    void initializeDefaultSettingsFor_ValidUserAccount_ReturnsSettingWithDefaultValues() {
        // given
        // when
        Setting result = settingDomainService.initializeDefaultSettingsFor(customerA);

        // then
        assertThat(result).isNotNull();

        assertThat(result.getUserAccount())
                .isSameAs(customerA);

        assertThat(result.getSettings())
                .isEqualTo(UserSettings.defaults());
    }
}
