package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.account.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.domain.model.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountStatus;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
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
public class UserAccountVerifyTest extends AbstractServiceTest {

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountTokenService userAccountTokenService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountVerify userAccountVerify;

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

        UserAccountToken activationToken = new UserAccountToken(user);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenService.validateToken(anyString())).thenReturn(activationToken);
        when(userAccountTokenRepository.save(any(UserAccountToken.class))).thenReturn(activationToken);
        when(userAccountRepository.save(any(User.class))).thenReturn(user);

        userAccountVerify.execute(activationToken.getToken());

        // then
        //        verify(accountRepository, times(1)).save(user);
        Assertions.assertThat(activationToken.isUsed()).isEqualTo(true);
        Assertions.assertThat(user.getAccountStatus()).isEqualTo(UserAccountStatus.VERIFIED);
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
                .setAccountStatus(UserAccountStatus.SUSPENDED);

        UserAccountToken activationToken = new UserAccountToken(user);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenService.validateToken(anyString())).thenReturn(activationToken);
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerify.execute(activationToken.getToken())
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
                .setAccountStatus(UserAccountStatus.VERIFIED);

        UserAccountToken activationToken = new UserAccountToken(user);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenService.validateToken(anyString())).thenReturn(activationToken);
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerify.execute(activationToken.getToken())
        );
    }
}
