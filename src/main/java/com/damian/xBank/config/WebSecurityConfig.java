package com.damian.xBank.config;

import com.damian.xBank.config.security.AuthenticationFilter;
import com.damian.xBank.config.security.CustomAuthenticationEntryPoint;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${app.frontend.host}")
    private String host;

    @Value("${app.frontend.port}")
    private String port;

    public WebSecurityConfig(
            AuthenticationFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint entryPoint
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabled for jwt
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ASYNC) // Avoid Authexception with notifications
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/users/register",
                                "/api/v1/accounts/verification/**",
                                "/api/v1/accounts/password/reset/**",
                                "/api/v1/banking/cards/authorize",
                                "/api/v1/banking/cards/capture",
                                "/payments/*/checkout",
                                "/payments/checkout",
                                "/payments/*/status",
                                "/payment-intents",
                                "/ws/**"
                        )
                        .permitAll()
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .authenticationProvider(authenticationProvider)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://" + host + ":" + port);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}