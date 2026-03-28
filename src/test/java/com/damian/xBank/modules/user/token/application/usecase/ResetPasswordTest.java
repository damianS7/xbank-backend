package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.usecase.password.reset.ResetPassword;
import com.damian.xBank.modules.user.token.application.usecase.password.reset.ResetPasswordCommand;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenPasswordResetNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.modules.user.user.infrastructure.service.UserPasswordService;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ResetPasswordTest extends AbstractServiceTest {

    @Mock
    private UserTokenPasswordResetNotifier userTokenPasswordResetNotifier;

    @Mock
    private UserPasswordService userPasswordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private ResetPassword resetPassword;

    @Spy
    private UserTokenFactory userTokenFactory;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should set a new password when token is valid")
    void resetPassword_WhenValidToken_ResetsPasswordAndMarksTokenAsUsed() {
        // given
        UserToken token = userTokenFactory.passwordToken(user);

        ResetPasswordCommand command = new ResetPasswordCommand(
            token.getToken(),
            "1111000"
        );

        // when
        when(userTokenService.validateToken(token.getToken()))
            .thenReturn(token);

        resetPassword.execute(command);

        // then
        assertThat(token.isUsed()).isTrue();
    }
}
