package com.damian.xBank.config.security;

import com.damian.xBank.modules.auth.infrastructure.service.CustomUserDetailsService;
import com.damian.xBank.shared.exception.JwtTokenExpiredException;
import com.damian.xBank.shared.exception.JwtTokenInvalidException;
import com.damian.xBank.shared.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que maneja la autenticación de cada Request.
 * <p>
 * Si encuentra el token jwt comprueba que sea válido y lo agrega al contexto.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public AuthenticationFilter(
        JwtUtil jwtUtil,
        CustomUserDetailsService customUserDetailsService,
        AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    /**
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
        @NonNull
        HttpServletRequest request,
        @NonNull
        HttpServletResponse response,
        @NonNull
        FilterChain filterChain
    )
        throws ServletException, IOException {
        // Token del Request.
        final String jwtToken = this.extractToken(request);

        // Si el token no existe o es una cada vacía ...
        if (jwtToken == null || jwtToken.isEmpty()) {
            log.debug("No jwt token detected. processing request without auth.");
            filterChain.doFilter(request, response);
            return;
        }

        // En caso de haber token, comprueba su validez.
        if (!jwtUtil.isTokenValid(jwtToken)) {
            log.debug("Jwt token is invalid.");
            // Si es invalido lanza 401.
            authenticationEntryPoint.commence(
                request, response, new JwtTokenInvalidException()
            );
            return;
        }

        // Comprobar que el token no esta expirado.
        if (jwtUtil.isTokenExpired(jwtToken)) {
            log.debug("Jwt token is expired.");
            // Si el token jwt expirô lanza 401.
            authenticationEntryPoint.commence(
                request, response, new JwtTokenExpiredException()
            );
            return;
        }

        // Obtenemos el email del token jwt
        final String email = jwtUtil.extractEmail(jwtToken);

        // Si el email no es null y el contexto no tiene usuario agregado ...
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = customUserDetailsService.loadUserByEmail(email);
            } catch (BadCredentialsException exception) {
                // Sino existe usuario con el email indicado lanza 401.
                log.debug("Failed to authenticate user with email: {}", email);
                authenticationEntryPoint.commence(request, response, exception);
                return;
            }

            // Token de autenticación.
            var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

            authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continue the filter chain.
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Buscar el token el header del request
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // Buscar el token en cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // No se encontró ningún token
        return null;
    }
}