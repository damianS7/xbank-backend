package com.damian.xBank.modules.user.token;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.domain.model.UserTokenType;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
import com.damian.xBank.modules.user.token.infrastructure.service.UserTokenService;
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
    private UserTokenService userTokenService;

    @Mock
    private UserTokenRepository userTokenRepository;

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
        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setToken("token");
        userToken.setCreatedAt(Instant.now());
        userToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        userToken.setToken("token");

        // when
        when(userTokenRepository.findByToken(userToken.getToken())).thenReturn(Optional.of(
                userToken));
        UserToken result = userTokenService.validateToken(userToken.getToken());

        // then
        verify(userTokenRepository, times(1)).findByToken(userToken.getToken());
        assertEquals(result.getToken(), userToken.getToken());
    }

    @Test
    @DisplayName("Should not validate when token not exists")
    void shouldNotValidateTokenWhenNotExists() {
        // given
        // when
        when(userTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(
                UserTokenNotFoundException.class,
                () -> userTokenService.validateToken(anyString())
        );

        // then
        verify(userTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not validate when token expired")
    void shouldNotValidateTokenWhenExpired() {
        // given
        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setToken("token");
        userToken.setCreatedAt(Instant.now());
        userToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        userToken.setToken("token");
        // when
        when(userTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userToken));
        assertThrows(
                UserTokenExpiredException.class,
                () -> userTokenService.validateToken(anyString())
        );

        // then
        verify(userTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not validate when token is used")
    void shouldNotValidateTokenIsUsed() {
        // given

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setUsed(true);
        userToken.setToken("token");
        userToken.setCreatedAt(Instant.now());
        userToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        userToken.setToken("token");
        // when
        when(userTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userToken));
        assertThrows(
                UserTokenUsedException.class,
                () -> userTokenService.validateToken(anyString())
        );

        // then
        verify(userTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should generate account activation token")
    void shouldGenerateAccountActivationToken() {
        // given
        UserToken givenActivationToken = UserToken.create()
                                                  .setUser(user)
                                                  .setToken("activation-token")
                                                  .setType(UserTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(userTokenRepository.findByUser_Id(user.getId()))
                .thenReturn(Optional.of(givenActivationToken));
        when(userTokenRepository.save(any(UserToken.class)))
                .thenReturn(givenActivationToken);

        UserToken
                generatedToken
                = userTokenService.generateVerificationToken(user.getEmail());

        // then
        assertThat(generatedToken)
                .isNotNull()
                .extracting(UserToken::isUsed)
                .isEqualTo(false);

        verify(userTokenRepository, times(1)).findByUser_Id(user.getId());
        verify(userAccountRepository, times(1)).findByEmail(user.getEmail());
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
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
        when(userTokenRepository.save(any(UserToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserToken generatedToken = userTokenService.generatePasswordResetToken(passwordResetRequest);

        // then
        assertThat(generatedToken)
                .isNotNull();
        assertThat(generatedToken.getToken().length()).isGreaterThanOrEqualTo(5);
        verify(userTokenRepository, times(1)).save(any(UserToken.class));
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

        UserToken token = new UserToken(user);
        token.setToken(token.generateToken());
        token.setType(UserTokenType.RESET_PASSWORD);

        // when
        when(userAccountRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(
                UserAccountNotFoundException.class,
                () -> userTokenService.generatePasswordResetToken(passwordResetRequest)
        );

        // then
        verify(userAccountRepository, times(1)).findByEmail(anyString());
    }
}
