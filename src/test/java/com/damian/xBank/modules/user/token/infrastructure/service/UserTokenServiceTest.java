package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.modules.user.token.domain.exception.UserTokenExpiredException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenNotFoundException;
import com.damian.xBank.modules.user.token.domain.exception.UserTokenUsedException;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.token.infrastructure.repository.UserTokenRepository;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserTokenServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserTokenService userTokenService;

    @Mock
    private UserTokenRepository userTokenRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withPassword(RAW_PASSWORD)
                              .withEmail("customer@demo.com")
                              .build();
    }

    @Test
    @DisplayName("should return token when valid")
    void validateToken_WhenValidToken_ReturnsToken() {
        // given
        UserToken userToken = new UserToken(user);
        userToken.generateVerificationToken();

        // when
        when(userTokenRepository.findByToken(userToken.getToken()))
                .thenReturn(Optional.of(userToken));

        UserToken result = userTokenService.validateToken(userToken.getToken());

        // then
        assertThat(result)
                .isNotNull()
                .extracting(UserToken::getToken)
                .isEqualTo(userToken.getToken());
        verify(userTokenRepository, times(1)).findByToken(userToken.getToken());
    }

    @Test
    @DisplayName("should throw exception when given token not exists")
    void validateToken_WhenNotExists_ThrowsException() {
        // given
        // when
        when(userTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        UserTokenNotFoundException exception = assertThrows(
                UserTokenNotFoundException.class,
                () -> userTokenService.validateToken(anyString())
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.USER_TOKEN_NOT_FOUND);

        verify(userTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("should throw exception when given token expired")
    void validateToken_WhenTokenExpired_ThrowsException() {
        // given
        UserToken userToken = new UserToken(user);
        userToken.generateVerificationToken();
        userToken.setCreatedAt(Instant.now());
        userToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));

        // when
        when(userTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userToken));

        UserTokenExpiredException exception = assertThrows(
                UserTokenExpiredException.class,
                () -> userTokenService.validateToken(anyString())
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.USER_TOKEN_EXPIRED);
        verify(userTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("should throw exception when given token is used")
    void validateToken_WhenTokenUsed_ThrowsException() {
        // given
        UserToken userToken = new UserToken(user);
        userToken.generateVerificationToken();
        userToken.setUsed(true);

        // when
        when(userTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userToken));

        UserTokenUsedException exception = assertThrows(
                UserTokenUsedException.class,
                () -> userTokenService.validateToken(anyString())
        );

        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.USER_TOKEN_USED);

        verify(userTokenRepository, times(1)).findByToken(anyString());
    }
}
