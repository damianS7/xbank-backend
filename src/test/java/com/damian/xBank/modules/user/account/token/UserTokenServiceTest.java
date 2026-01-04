package com.damian.xBank.modules.user.account.token;

import com.damian.xBank.modules.user.account.token.domain.exception.UserAccountTokenExpiredException;
import com.damian.xBank.modules.user.account.token.domain.exception.UserAccountTokenNotFoundException;
import com.damian.xBank.modules.user.account.token.domain.exception.UserAccountTokenUsedException;
import com.damian.xBank.modules.user.account.token.domain.model.UserAccountToken;
import com.damian.xBank.modules.user.account.token.domain.model.UserAccountTokenType;
import com.damian.xBank.modules.user.account.token.infrastructure.repository.UserAccountTokenRepository;
import com.damian.xBank.modules.user.account.token.infrastructure.service.UserAccountTokenService;
import com.damian.xBank.modules.user.user.application.dto.request.UserAccountPasswordResetRequest;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

public class UserTokenServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountTokenService userAccountTokenService;

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

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
    @DisplayName("Should validate token")
    void shouldValidateToken() {
        // given
        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(user);
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(userAccountToken.getToken())).thenReturn(Optional.of(
                userAccountToken));
        UserAccountToken result = userAccountTokenService.validateToken(userAccountToken.getToken());

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(userAccountToken.getToken());
        assertEquals(result.getToken(), userAccountToken.getToken());
    }

    @Test
    @DisplayName("Should not validate when token not exists")
    void shouldNotValidateTokenWhenNotExists() {
        // given
        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(
                UserAccountTokenNotFoundException.class,
                () -> userAccountTokenService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not validate when token expired")
    void shouldNotValidateTokenWhenExpired() {
        // given
        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(user);
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");
        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userAccountToken));
        assertThrows(
                UserAccountTokenExpiredException.class,
                () -> userAccountTokenService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not validate when token is used")
    void shouldNotValidateTokenIsUsed() {
        // given

        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(user);
        userAccountToken.setUsed(true);
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");
        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userAccountToken));
        assertThrows(
                UserAccountTokenUsedException.class,
                () -> userAccountTokenService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should generate account activation token")
    void shouldGenerateAccountActivationToken() {
        // given
        UserAccountToken givenActivationToken = UserAccountToken.create()
                                                                .setAccount(user)
                                                                .setToken("activation-token")
                                                                .setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(userAccountTokenRepository.findByAccount_Id(user.getId()))
                .thenReturn(Optional.of(givenActivationToken));
        when(userAccountTokenRepository.save(any(UserAccountToken.class)))
                .thenReturn(givenActivationToken);

        UserAccountToken
                generatedToken
                = userAccountTokenService.generateVerificationToken(user.getEmail());

        // then
        assertThat(generatedToken)
                .isNotNull()
                .extracting(UserAccountToken::isUsed)
                .isEqualTo(false);

        verify(userAccountTokenRepository, times(1)).findByAccount_Id(user.getId());
        verify(userAccountRepository, times(1)).findByEmail(user.getEmail());
        verify(userAccountTokenRepository, times(1)).save(any(UserAccountToken.class));
    }

    @Test
    @DisplayName("Should generate password reset token")
    void shouldGeneratePasswordResetToken() {
        // given

        UserAccountPasswordResetRequest passwordResetRequest = new UserAccountPasswordResetRequest(
                user.getEmail()
        );

        // when
        when(userAccountRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userAccountTokenRepository.save(any(UserAccountToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccountToken generatedToken = userAccountTokenService.generatePasswordResetToken(passwordResetRequest);

        // then
        assertThat(generatedToken)
                .isNotNull();
        assertThat(generatedToken.getToken().length()).isGreaterThanOrEqualTo(5);
        verify(userAccountTokenRepository, times(1)).save(any(UserAccountToken.class));
    }

    @Test
    @DisplayName("Should not generate password reset token when account not found")
    void shouldNotGeneratePasswordResetTokenWhenAccountNotFound() {
        // given
        User user = User
                .create()
                .setId(10L)
                .setEmail("user@demo.com")
                .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD));

        UserAccountPasswordResetRequest passwordResetRequest = new UserAccountPasswordResetRequest(
                user.getEmail()
        );

        UserAccountToken token = new UserAccountToken(user);
        token.setToken(token.generateToken());
        token.setType(UserAccountTokenType.RESET_PASSWORD);

        // when
        when(userAccountRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(
                UserAccountNotFoundException.class,
                () -> userAccountTokenService.generatePasswordResetToken(passwordResetRequest)
        );

        // then
        verify(userAccountRepository, times(1)).findByEmail(anyString());
    }
}
