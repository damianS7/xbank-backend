package com.damian.xBank.modules.auth.service;

import com.damian.xBank.modules.auth.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.dto.AuthenticationResponse;
import com.damian.xBank.modules.auth.exception.AccountNotVerifiedException;
import com.damian.xBank.modules.auth.exception.AccountSuspendedException;
import com.damian.xBank.modules.user.account.account.UserAccountStatus;
import com.damian.xBank.shared.domain.UserPrincipal;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Manages user authentication flow with login validation and token generation.
 * Performs account status checks and enforces security policies.
 */
@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager
    ) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Controls the login flow.
     *
     * @param request Contains the fields needed to login into the service
     * @return Contains the data (User, Profile) and the token
     * @throws BadCredentialsException     if credentials are invalid
     * @throws AccountNotVerifiedException if the account is not verified
     */
    public AuthenticationResponse login(AuthenticationRequest request) {
        final String email = request.email();
        final String password = request.password();
        final Authentication auth;

        // Authenticate the user
        auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, password)
        );

        // Get the authenticated user
        final UserPrincipal currentUser = ((UserPrincipal) auth.getPrincipal());
        final HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", currentUser.getEmail());
        claims.put("role", currentUser.getRole());

        // Generate a token for the authenticated user
        final String token = jwtUtil.generateToken(
                claims,
                email
        );

        // check if the account is disabled
        if (currentUser.getAccount().getAccountStatus().equals(UserAccountStatus.SUSPENDED)) {
            throw new AccountSuspendedException(
                    Exceptions.ACCOUNT.SUSPENDED
            );
        }

        // check if the account is verified
        if (currentUser.getAccount().getAccountStatus().equals(UserAccountStatus.PENDING_VERIFICATION)) {
            throw new AccountNotVerifiedException(
                    Exceptions.ACCOUNT.NOT_VERIFIED
            );
        }

        // Return the user data and the token
        log.info("Login successful for user: {}", email);
        return new AuthenticationResponse(token);
    }
}
