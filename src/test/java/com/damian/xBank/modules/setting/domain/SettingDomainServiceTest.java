package com.damian.xBank.modules.setting.domain;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
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
