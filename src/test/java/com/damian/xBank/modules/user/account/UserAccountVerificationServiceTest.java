package com.damian.xBank.modules.user.account;

import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.account.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.account.account.model.UserAccount;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.xBank.modules.user.account.token.enums.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.service.UserAccountTokenService;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class UserAccountVerificationServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserAccountTokenService userAccountTokenService;

    @InjectMocks
    private UserAccountVerificationService userAccountVerificationService;

    @Test
    @DisplayName("Should activate account")
    void shouldVerifyAccount() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountToken activationToken = new UserAccountToken(userAccount);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenService.validateToken(anyString())).thenReturn(activationToken);
        when(userAccountTokenRepository.save(any(UserAccountToken.class))).thenReturn(activationToken);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        //        when(userRepository.save(any(User.class))).thenReturn(user);
        userAccountVerificationService.verifyAccount(activationToken.getToken());

        // then
        //        verify(accountRepository, times(1)).save(user);
        assertThat(activationToken.isUsed()).isEqualTo(true);
        assertThat(userAccount.getAccountStatus()).isEqualTo(UserAccountStatus.VERIFIED);
    }

    @Test
    @DisplayName("Should not activate account when account is Suspended")
    void shouldNotVerifyAccountWhenAccountIsSuspended() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                .setAccountStatus(UserAccountStatus.SUSPENDED);

        UserAccountToken activationToken = new UserAccountToken(userAccount);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenService.validateToken(anyString())).thenReturn(activationToken);
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerificationService.verifyAccount(activationToken.getToken())
        );
    }

    @Test
    @DisplayName("Should not activate account when account is active")
    void shouldNotVerifyAccountWhenAccountIsActive() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                .setAccountStatus(UserAccountStatus.VERIFIED);

        UserAccountToken activationToken = new UserAccountToken(userAccount);
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenService.validateToken(anyString())).thenReturn(activationToken);
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerificationService.verifyAccount(activationToken.getToken())
        );
    }
}
