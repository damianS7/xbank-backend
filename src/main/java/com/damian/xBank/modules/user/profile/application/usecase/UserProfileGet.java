package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.cqrs.result.UserProfileResult;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserProfileGet {
    private static final Logger log = LoggerFactory.getLogger(UserProfileGet.class);
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationContext authenticationContext;

    public UserProfileGet(
        UserProfileRepository userProfileRepository,
        AuthenticationContext authenticationContext
    ) {
        this.userProfileRepository = userProfileRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a current user profile
     *
     * @return the user profile
     * @throws UserProfileNotFoundException
     */
    public UserProfileResult execute() {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // if the user does not exist we throw an exception
        UserProfile profile = userProfileRepository
            .findByUserId(currentUser.getId())
            .orElseThrow(
                () -> new UserProfileNotFoundException(currentUser.getId())
            );

        return UserProfileResult.from(profile);
    }
}
