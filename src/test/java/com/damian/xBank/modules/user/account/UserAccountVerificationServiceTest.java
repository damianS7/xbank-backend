package com.damian.xBank.modules.user.account;

import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.account.exception.UserAccountVerificationNotPendingException;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.xBank.modules.user.account.token.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenExpiredException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenNotFoundException;
import com.damian.xBank.modules.user.account.token.exception.UserAccountTokenUsedException;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.domain.UserAccountToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserAccountVerificationServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountVerificationService userAccountVerificationService;

    @Test
    @DisplayName("Should generate account activation token")
    void shouldGenerateAccountActivationToken() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setEmail("userAccount@test.com")
                .setPassword(passwordEncoder.encode(passwordEncoder.encode(RAW_PASSWORD)));

        UserAccountToken givenActivationToken = UserAccountToken.create()
                                                                .setAccount(userAccount)
                                                                .setToken("activation-token")
                                                                .setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountRepository.findByEmail(userAccount.getEmail()))
                .thenReturn(Optional.of(userAccount));
        when(userAccountTokenRepository.findByAccount_Id(userAccount.getId()))
                .thenReturn(Optional.of(givenActivationToken));
        when(userAccountTokenRepository.save(any(UserAccountToken.class)))
                .thenReturn(givenActivationToken);

        UserAccountToken
                generatedToken
                = userAccountVerificationService.generateVerificationToken(userAccount.getEmail());

        // then
        assertThat(generatedToken)
                .isNotNull()
                .extracting(UserAccountToken::isUsed)
                .isEqualTo(false);

        verify(userAccountTokenRepository, times(1)).findByAccount_Id(userAccount.getId());
        verify(userAccountRepository, times(1)).findByEmail(userAccount.getEmail());
        verify(userAccountTokenRepository, times(1)).save(any(UserAccountToken.class));
    }

    @Test
    @DisplayName("Should verify token")
    void shouldValidateToken() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(userAccount);
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(userAccountToken.getToken())).thenReturn(Optional.of(
                userAccountToken));
        UserAccountToken result = userAccountVerificationService.validateToken(userAccountToken.getToken());

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(userAccountToken.getToken());
        assertEquals(result.getToken(), userAccountToken.getToken());
    }

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
        when(userAccountTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
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
        when(userAccountTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
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
        when(userAccountTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerificationService.verifyAccount(activationToken.getToken())
        );
    }

    @Test
    @DisplayName("Should not verify token when is wrong")
    void shouldNotVerifyAccountWhenTokenIsWrong() {
        // given
        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(
                UserAccountTokenNotFoundException.class,
                () -> userAccountVerificationService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not verify token when is expired")
    void shouldNotVerifyAccountWhenTokenIsExpired() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                .setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);

        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(userAccount);
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userAccountToken));
        assertThrows(
                UserAccountTokenExpiredException.class,
                () -> userAccountVerificationService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not verify token when is already used")
    void shouldNotVerifyAccountWhenTokenIsUsed() {
        // given
        UserAccount userAccount = UserAccount
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                .setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);

        UserAccountToken userAccountToken = UserAccountToken.create()
                                                            .setUsed(true)
                                                            .setAccount(userAccount)
                                                            .setCreatedAt(Instant.now())
                                                            .setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                                                            .setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userAccountToken));
        assertThrows(
                UserAccountTokenUsedException.class,
                () -> userAccountVerificationService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }
}
