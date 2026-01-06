package com.damian.xBank.modules.setting.domain;

import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.setting.infrastructure.persistence.repository.SettingRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
}
