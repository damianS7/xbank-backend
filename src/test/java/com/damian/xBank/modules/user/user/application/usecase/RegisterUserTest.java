package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUser;
import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUserCommand;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.service.UserDomainService;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserProfileTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RegisterUserTest extends AbstractServiceTest {
    @Mock
    private UserTokenVerificationNotifier userTokenVerificationNotifier;

    @Mock
    private UserTokenLinkBuilder userTokenLinkBuilder;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserDomainService userDomainService = new UserDomainService(bCryptPasswordEncoder);

    @Spy
    private UserProfileTestFactory userProfileTestFactory;

    @Spy
    private UserTokenFactory userTokenFactory;

    @InjectMocks
    private RegisterUser registerUser;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should register user when valid request")
    void registerUser_WhenValidRequest_SavesUser() {
        // given
        RegisterUserCommand command = new RegisterUserCommand(
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
        registerUser.execute(command);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User userCaptured = userArgumentCaptor.getValue();

        assertThat(userCaptured)
            .isNotNull()
            .extracting(
                User::getEmail
            ).isEqualTo(
                command.email()
            );
        verify(userRepository, times(1)).save(any(User.class));
    }
}
