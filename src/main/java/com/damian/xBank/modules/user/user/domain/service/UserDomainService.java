package com.damian.xBank.modules.user.user.domain.service;

import com.damian.xBank.modules.user.user.domain.exception.UserEmailTakenException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserDomainService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDomainService(
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.bCryptPasswordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user
     *
     * @param email    email to be registered
     * @param password password
     * @return the user created
     * @throws UserEmailTakenException if another user has the email
     */
    public User createUser(String email, String password, UserRole role) {

        // we create the user and assign the data
        return User.create()
                   .setEmail(email)
                   .setRole(role)
                   .setPassword(bCryptPasswordEncoder.encode(password))
                   .setCreatedAt(Instant.now());
    }
}
