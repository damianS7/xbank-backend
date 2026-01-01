package com.damian.xBank.modules.auth.application.usecase;

import com.damian.xBank.modules.auth.application.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.application.dto.AuthenticationResponse;
import com.damian.xBank.modules.auth.domain.exception.UserAccountNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountSuspendedException;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.security.User;
import com.damian.xBank.shared.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
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

public class AuthenticationLoginTest extends AbstractServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationLogin authenticationLogin;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setAccountStatus(UserAccountStatus.VERIFIED)
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);
    }


    @Test
    @DisplayName("should return token when credentials are valid")
    void login_WithValidCredentials_ReturnsToken() {
        // given
        Authentication authentication = mock(Authentication.class);
        String givenToken = "jwt-token";

        User user = new User(customer.getAccount());

        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                customer.getAccount().getPassword()
        );

        // when
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(anyMap(), anyString())).thenReturn(givenToken);
        when(authentication.getPrincipal()).thenReturn(user);

        AuthenticationResponse response = authenticationLogin.execute(request);

        // then
        assertThat(response)
                .isNotNull()
                .extracting(
                        AuthenticationResponse::token
                ).isEqualTo(givenToken);
    }

    @Test
    @DisplayName("should throw exception when credentials are invalid")
    void login_WithInvalidCredentials_ThrowsException() {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                customer.getAccount().getPassword()
        );

        // when
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException(ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationLogin.execute(request)
        );

        // Then
        assertEquals(ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when account is suspended")
    void login_WhenAccountSuspended_ThrowsException() {
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
                () -> authenticationLogin.execute(request)
        );

        // Then
        assertEquals(ErrorCodes.USER_ACCOUNT_SUSPENDED, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when account is disabled or not verified")
    void login_WhenAccountNotVerified_ThrowsException() {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                customer.getEmail(),
                customer.getAccount().getPassword()
        );

        // when
        when(authenticationManager.authenticate(any())).thenThrow(
                new UserAccountNotVerifiedException(request.email())
        );

        UserAccountNotVerifiedException exception = assertThrows(
                UserAccountNotVerifiedException.class,
                () -> authenticationLogin.execute(request)
        );

        // Then
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_VERIFIED, exception.getMessage());
    }
}
