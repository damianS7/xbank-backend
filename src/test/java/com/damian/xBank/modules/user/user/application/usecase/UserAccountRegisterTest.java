package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountRegistrationRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.service.UserAccountDomainService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountRegisterTest extends AbstractServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private SettingDomainService settingDomainService;

    @Mock
    private UserAccountDomainService userAccountDomainService;

    @InjectMocks
    private UserAccountRegister userAccountRegister;

    private User customer;

    @BeforeEach
    void setUp() {

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .withEmail("customer@demo.com")
                                  .withProfile(UserProfileTestFactory.aProfile())
                                  .build();
    }

    @Test
    @DisplayName("Should create customer")
    void shouldRegisterCustomer() {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@gmail.com",
                "123456",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        User givenUser = User.create()
                             .setEmail(request.email());

        // when
        when(settingDomainService.initializeDefaultSettingsFor(any(User.class)))
                .thenReturn(Setting.create(givenUser));

        when(userAccountDomainService.createUserAccount(anyString(), anyString(), any()))
                .thenReturn(givenUser);

        userAccountRegister.execute(request);

        // then
        ArgumentCaptor<User> customerArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(customerArgumentCaptor.capture());

        User customer = customerArgumentCaptor.getValue();
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(customer)
                .isNotNull()
                .extracting(
                        User::getEmail
                ).isEqualTo(
                        request.email()
                );
    }
}
