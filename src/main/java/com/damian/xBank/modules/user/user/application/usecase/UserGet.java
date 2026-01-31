package com.damian.xBank.modules.user.user.application.usecase;

import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserGet {
    private static final Logger log = LoggerFactory.getLogger(UserGet.class);
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public UserGet(
            UserRepository userRepository,
            AuthenticationContext authenticationContext
    ) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a current user data
     *
     * @return the user
     * @throws UserNotFoundException
     */
    public User execute() {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // if the user does not exist we throw an exception
        return userRepository
                .findById(currentUser.getId())
                .orElseThrow(
                        () -> new UserNotFoundException(currentUser.getId())
                );
    }
}
