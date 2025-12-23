package com.damian.xBank.modules.auth.application.service;

import com.damian.xBank.modules.auth.application.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.application.dto.AuthenticationResponse;
import com.damian.xBank.modules.auth.domain.exception.UserAccountNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountSuspendedException;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.security.User;
import com.damian.xBank.shared.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest extends AbstractServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("should login when valid credentials")
    void shouldLoginWhenValidCredentials() {
        // given
        Authentication authentication = mock(Authentication.class);
        String givenToken = "jwt-token";

        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("user@demo.com")
                                             .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                             .setAccountStatus(UserAccountStatus.VERIFIED);

        User user = new User(userAccount);

        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        // when
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(anyMap(), anyString())).thenReturn(givenToken);
        when(authentication.getPrincipal()).thenReturn(user);

        AuthenticationResponse response = authenticationService.login(request);

        // then
        assertThat(response)
                .isNotNull()
                .extracting(
                        AuthenticationResponse::token
                ).isEqualTo(givenToken);
    }

    @Test
    @DisplayName("should not login when invalid credentials")
    void shouldNotLoginWhenInvalidCredentials() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("user@demo.com")
                                             .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                             .setAccountStatus(UserAccountStatus.VERIFIED);

        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        // when
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException(ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(request)
        );

        // Then
        assertEquals(ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS, exception.getMessage());
    }

    @Test
    @DisplayName("should not login when account is locked or suspended")
    void shouldNotLoginWhenAccountIsLockedOrSuspended() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("user@demo.com")
                                             .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                             .setAccountStatus(UserAccountStatus.SUSPENDED);

        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        // when
        when(authenticationManager.authenticate(any())).thenThrow(
                new UserAccountSuspendedException(userAccount.getEmail())
        );

        UserAccountSuspendedException exception = assertThrows(
                UserAccountSuspendedException.class,
                () -> authenticationService.login(request)
        );

        // Then
        assertEquals(ErrorCodes.USER_ACCOUNT_SUSPENDED, exception.getMessage());
    }

    @Test
    @DisplayName("should not login when account is disabled or not verified")
    void shouldNotLoginWhenAccountIsDisabledOrNotVerified() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("user@demo.com")
                                             .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                             .setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);

        AuthenticationRequest request = new AuthenticationRequest(
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        // when
        when(authenticationManager.authenticate(any())).thenThrow(
                new UserAccountNotVerifiedException(userAccount.getEmail())
        );

        UserAccountNotVerifiedException exception = assertThrows(
                UserAccountNotVerifiedException.class,
                () -> authenticationService.login(request)
        );

        // Then
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_VERIFIED, exception.getMessage());
    }
}
