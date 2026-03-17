package com.damian.xBank.modules.auth.application.usecase;

import com.damian.xBank.modules.auth.domain.exception.UserNotVerifiedException;
import com.damian.xBank.modules.auth.domain.exception.UserSuspendedException;
import com.damian.xBank.modules.auth.infrastructure.rest.request.AuthenticationRequest;
import com.damian.xBank.modules.auth.infrastructure.rest.response.AuthenticationResponse;
import com.damian.xBank.shared.security.UserPrincipal;
import com.damian.xBank.shared.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Caso de uso que controla el inicio de sesión.
 * <p>
 * Realiza comprobaciones de seguridad.
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
     *
     * @param request Request con los datos para inicio de sesión.
     * @return La respuesta con el token.
     * @throws BadCredentialsException  si los credenciales son incorrectos.
     * @throws UserNotVerifiedException si la cuenta no esta verificada.
     */
    public AuthenticationResponse execute(AuthenticationRequest request) {
        final String email = request.email();
        final String password = request.password();

        Authentication auth = null;

        try {
            auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            throw new UserNotVerifiedException(email);
        } catch (LockedException e) {
            throw new UserSuspendedException(email);
        }

        final UserPrincipal principal = ((UserPrincipal) auth.getPrincipal());
        final HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", principal.getEmail());
        claims.put("role", principal.getRole());

        // Genera un token de inicio de sesión.
        final String token = jwtUtil.generateToken(
            claims,
            email
        );

        return new AuthenticationResponse(token);
    }
}
