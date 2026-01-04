package com.damian.xBank.modules.user.user.domain.service;

import com.damian.xBank.modules.user.user.domain.exception.UserEmailTakenException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserDomainService {
    private static final Logger log = LoggerFactory.getLogger(UserDomainService.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDomainService(
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
     * @throws UserEmailTakenException if another user has the email
     */
    public User createUserAccount(String email, String password, UserRole role) {

        // we create the user and assign the data
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setCreatedAt(Instant.now());

        return user;
    }


}
