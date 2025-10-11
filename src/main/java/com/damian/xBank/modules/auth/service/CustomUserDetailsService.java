package com.damian.xBank.modules.auth.service;

import com.damian.xBank.modules.auth.exception.EmailNotFoundException;
import com.damian.xBank.modules.user.user.repository.UserRepository;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.domain.UserPrincipal;
import com.damian.xBank.shared.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByEmail(username);
    }

    public UserDetails loadUserByEmail(String email) throws EmailNotFoundException {
        UserAccount user = userRepository
                .findByUserAccount_Email(email)
                .orElseThrow(
                        () -> {
                            log.debug("Failed to find a user with email: {}", email);
                            return new EmailNotFoundException(
                                    Exceptions.ACCOUNT.BAD_CREDENTIALS, email
                            );
                        }
                );

        return new UserPrincipal(user);
    }
}
