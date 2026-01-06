package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.dto.request.UserTokenRequestPasswordResetRequest;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenLinkBuilder;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenPasswordNotifier;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserTokenRequestPasswordResetTest extends AbstractServiceTest {

    @Mock
    private UserTokenPasswordNotifier userTokenPasswordNotifier;

    @Mock
    private UserTokenLinkBuilder userTokenLinkBuilder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private UserTokenRequestPasswordReset userTokenRequestPasswordReset;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                              .withEmail("customerA@demo.com")
                              .build();
    }

    @Test
    @DisplayName("should request a password reset and sent it to email")
    void requestPasswordReset_WhenValidRequest_SendsEmail() {
        // given
        UserTokenRequestPasswordResetRequest request = new UserTokenRequestPasswordResetRequest(
                user.getEmail()
        );

        // when
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(userTokenRepository.save(any(UserToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userTokenRequestPasswordReset.execute(request);

        // then
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void requestPasswordReset_WhenUserNotFound_ThrowsException() {
        // given
        UserTokenRequestPasswordResetRequest request = new UserTokenRequestPasswordResetRequest(
                user.getEmail()
        );

        // when
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userTokenRequestPasswordReset.execute(request)
        );

        // then
        verify(userRepository, times(1)).findByEmail(anyString());
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.USER_NOT_FOUND);
    }
}
