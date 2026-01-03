package com.damian.xBank.modules.auth.application.usecase;

import com.damian.xBank.modules.auth.application.dto.AuthenticationRequest;
import com.damian.xBank.modules.auth.application.dto.AuthenticationResponse;
import com.damian.xBank.modules.auth.domain.exception.UserAccountNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserAccountSuspendedException;
import com.damian.xBank.shared.security.UserPrincipal;
import com.damian.xBank.shared.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Manages user authentication flow with login validation and token generation.
 * Performs account status checks and enforces security policies.
 */
@Service
public class AuthenticationLogin {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationLogin.class);
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationLogin(
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
     * @throws BadCredentialsException         if credentials are invalid
     * @throws UserAccountNotVerifiedException if the account is not verified
     */
    public AuthenticationResponse execute(AuthenticationRequest request) {
        final String email = request.email();
        final String password = request.password();

        //                final Authentication auth = authenticationManager.authenticate(
        //                        new UsernamePasswordAuthenticationToken(email, password)
        //                );

        Authentication auth = null;

        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            throw new UserAccountNotVerifiedException(email);
        } catch (LockedException e) {
            throw new UserAccountSuspendedException(email);
        }


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

        // Return the user data and the token
        log.info("Login successful for user: {}", email);
        return new AuthenticationResponse(token);
    }
}
