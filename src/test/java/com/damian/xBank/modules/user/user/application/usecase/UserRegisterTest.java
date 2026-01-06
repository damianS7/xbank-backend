package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.setting.domain.factory.SettingFactory;
import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.application.dto.request.UserRegistrationRequest;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.service.UserDomainService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRegisterTest extends AbstractServiceTest {
    @Mock
    private UserTokenVerificationNotifier userTokenVerificationNotifier;

    @Mock
    private UserTokenLinkBuilder userTokenLinkBuilder;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserDomainService userDomainService = new UserDomainService(bCryptPasswordEncoder);

    @Spy
    private UserProfileFactory userProfileFactory;

    @Spy
    private UserTokenFactory userTokenFactory;

    @Spy
    private SettingFactory settingFactory;

    @InjectMocks
    private UserRegister userRegister;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withPassword(RAW_PASSWORD)
                              .withEmail("customer@demo.com")
                              .withProfile(UserProfileFactory.testProfile())
                              .build();
    }

    @Test
    @DisplayName("should register user when valid request")
    void registerUser_WhenValidRequest_SavesUser() {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
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

        // when
        userRegister.execute(request);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User userCaptured = userArgumentCaptor.getValue();

        assertThat(userCaptured)
                .isNotNull()
                .extracting(
                        User::getEmail
                ).isEqualTo(
                        request.email()
                );
        verify(userRepository, times(1)).save(any(User.class));
    }
}
