package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.application.usecase.verification.verify.VerifyAccount;
import com.damian.xBank.modules.user.token.application.usecase.verification.verify.VerifyAccountCommand;
import com.damian.xBank.modules.user.token.domain.factory.UserTokenFactory;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.notification.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestBuilder;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerifyAccountTest extends AbstractServiceTest {
    @Mock
    private UserTokenVerificationNotifier userTokenVerificationNotifier;

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerifyAccount verifyAccount;

    @Spy
    private UserTokenFactory userTokenFactory;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should verify account when token is valid")
    void verifyAccount_WhenValidToken_ActivatesAccount() {
        // given
        User unverifiedUser = UserTestBuilder
            .builder()
            .withEmail("user@demo.com")
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.PENDING_VERIFICATION)
            .withPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD))
            .build();

        UserToken token = userTokenFactory.verificationToken(unverifiedUser);

        VerifyAccountCommand command = new VerifyAccountCommand(token.getToken());

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(token);
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(token);
        when(userRepository.save(any(User.class))).thenReturn(unverifiedUser);

        verifyAccount.execute(command);

        // then
        verify(userRepository, times(1)).save(any(User.class));
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
        assertThat(token.isUsed()).isEqualTo(true);
        assertThat(unverifiedUser.getStatus()).isEqualTo(UserStatus.VERIFIED);
    }

    @Test
    @DisplayName("should throw exception when user is suspended")
    void verifyAccount_WhenUserIsSuspended_ThrowsException() {
        // given
        User user = UserTestBuilder
            .builder()
            .withEmail("user@demo.com")
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.SUSPENDED)
            .withPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD))
            .build();

        UserToken token = userTokenFactory.verificationToken(user);

        VerifyAccountCommand command = new VerifyAccountCommand(token.getToken());

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(token);

        UserVerificationNotPendingException exception = assertThrows(
            UserVerificationNotPendingException.class,
            () -> verifyAccount.execute(command)
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.USER_VERIFICATION_NOT_PENDING);
    }

    @Test
    @DisplayName("should throw exception when user is active")
    void verifyAccount_WhenUserIsVerified_ThrowsException() {
        // given
        User user = UserTestBuilder
            .builder()
            .withEmail("user@demo.com")
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .withPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD))
            .build();

        UserToken token = userTokenFactory.verificationToken(user);

        VerifyAccountCommand command = new VerifyAccountCommand(token.getToken());

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(token);
        UserVerificationNotPendingException exception = assertThrows(
            UserVerificationNotPendingException.class,
            () -> verifyAccount.execute(command)
        );

        assertThat(exception)
            .isNotNull()
            .hasMessage(ErrorCodes.USER_VERIFICATION_NOT_PENDING);
    }
}
