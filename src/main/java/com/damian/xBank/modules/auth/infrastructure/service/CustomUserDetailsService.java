package com.damian.xBank.modules.auth.infrastructure.service;

import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.account.account.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return loadUserByEmail(username);
    }

    public UserDetails loadUserByEmail(String email) {
        User user = userAccountRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> {
                            log.debug("Failed to find a user with email: {}", email);
                            return new BadCredentialsException(ErrorCodes.AUTH_LOGIN_BAD_CREDENTIALS);
                        }
                );

        return new UserPrincipal(user);
    }
}
