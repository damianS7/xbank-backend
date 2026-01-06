package com.damian.xBank.modules.user.token.application.usecase;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenVerificationNotifier;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import com.damian.xBank.shared.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTokenVerifyAccountTest extends AbstractServiceTest {
    @Mock
    private UserTokenVerificationNotifier userTokenVerificationNotifier;

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserTokenVerifyAccount userTokenVerifyAccount;

    private User customer;

    @BeforeEach
    void setUp() {

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(RAW_PASSWORD)
                                  .withEmail("customer@demo.com")
                                  .withProfile(UserProfileTestFactory.aProfile())
                                  .build();
    }

    @Test
    @DisplayName("should verify account when token is valid")
    void verifyAccount_WhenValidToken_ActivatesAccount() {
        // given
        User unverifiedUser = UserTestFactory.customer().setStatus(UserStatus.PENDING_VERIFICATION);

        UserToken token = new UserToken(unverifiedUser);
        token.generateVerificationToken();

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(token);
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(token);
        when(userRepository.save(any(User.class))).thenReturn(unverifiedUser);

        userTokenVerifyAccount.execute(token.getToken());

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
        User user = UserTestFactory.customer().setStatus(UserStatus.SUSPENDED);

        UserToken token = new UserToken(user);
        token.generateVerificationToken();

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(token);

        UserVerificationNotPendingException exception = assertThrows(
                UserVerificationNotPendingException.class,
                () -> userTokenVerifyAccount.execute(token.getToken())
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.USER_VERIFICATION_NOT_PENDING);
    }

    @Test
    @DisplayName("should throw exception when user is active")
    void verifyAccount_WhenUserIsActive_ThrowsException() {
        // given
        User user = UserTestFactory.customer().setStatus(UserStatus.VERIFIED);

        UserToken token = new UserToken(user);
        token.generateVerificationToken();

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(token);
        UserVerificationNotPendingException exception = assertThrows(
                UserVerificationNotPendingException.class,
                () -> userTokenVerifyAccount.execute(token.getToken())
        );

        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.USER_VERIFICATION_NOT_PENDING);
    }
}
