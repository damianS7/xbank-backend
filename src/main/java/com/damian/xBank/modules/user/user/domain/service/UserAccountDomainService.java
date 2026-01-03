package com.damian.xBank.modules.user.user.domain.service;

import com.damian.xBank.modules.user.user.domain.exception.UserAccountEmailTakenException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserAccountDomainService {
    private static final Logger log = LoggerFactory.getLogger(UserAccountDomainService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserAccountDomainService(
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Creates a new user
     *
     * @param email    email to be registered
     * @param password password
     * @return the user created
     * @throws UserAccountEmailTakenException if another user has the email
     */
    public User createUserAccount(String email, String password, UserAccountRole role) {

        // we create the user and assign the data
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setCreatedAt(Instant.now());

        return user;
    }


}
