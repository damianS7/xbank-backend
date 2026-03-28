package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.usecase.password.reset.RequestPasswordReset;
import com.damian.xBank.modules.user.token.application.usecase.password.reset.RequestPasswordResetCommand;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenPasswordResetNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.notification.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestPasswordResetTest extends AbstractServiceTest {

    @Mock
    private UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;

    @Mock
    private UserTokenLinkBuilder userTokenLinkBuilder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenRepository userTokenRepository;

    @Spy
    private UserTokenFactory userTokenFactory;

    @InjectMocks
    private RequestPasswordReset requestPasswordReset;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should request a password reset and sent it to email")
    void requestPasswordReset_WhenValidRequest_SendsEmail() {
        // given
        RequestPasswordResetCommand command = new RequestPasswordResetCommand(
            user.getEmail()
        );

        // when
        when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(user));

        when(userTokenRepository.save(any(UserToken.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        requestPasswordReset.execute(command);

        // then
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void requestPasswordReset_WhenUserNotFound_ThrowsException() {
        // given
        RequestPasswordResetCommand command = new RequestPasswordResetCommand(
            user.getEmail()
        );

        // when
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> requestPasswordReset.execute(command)
        );

        // then
        verify(userRepository, times(1)).findByEmail(anyString());
        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.USER_NOT_FOUND);
    }
}
