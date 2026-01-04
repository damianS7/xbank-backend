package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.token.application.usecase.UserTokenVerify;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.model.UserTokenType;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTokenVerifyTest extends AbstractServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserTokenVerify userTokenVerify;

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
    @DisplayName("Should activate account")
    void shouldVerifyAccount() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD));

        UserToken activationToken = new UserToken(user);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(activationToken);
        when(userTokenRepository.save(any(UserToken.class))).thenReturn(activationToken);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userTokenVerify.execute(activationToken.getToken());

        // then
        //        verify(accountRepository, times(1)).save(user);
        Assertions.assertThat(activationToken.isUsed()).isEqualTo(true);
        Assertions.assertThat(user.getStatus()).isEqualTo(UserStatus.VERIFIED);
    }

    @Test
    @DisplayName("Should not activate account when account is Suspended")
    void shouldNotVerifyAccountWhenAccountIsSuspended() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                .setStatus(UserStatus.SUSPENDED);

        UserToken activationToken = new UserToken(user);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(activationToken);
        assertThrows(
                UserVerificationNotPendingException.class,
                () -> userTokenVerify.execute(activationToken.getToken())
        );
    }

    @Test
    @DisplayName("Should not activate account when account is active")
    void shouldNotVerifyAccountWhenAccountIsActive() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                .setStatus(UserStatus.VERIFIED);

        UserToken activationToken = new UserToken(user);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userTokenService.validateToken(anyString())).thenReturn(activationToken);
        assertThrows(
                UserVerificationNotPendingException.class,
                () -> userTokenVerify.execute(activationToken.getToken())
        );
    }
}
