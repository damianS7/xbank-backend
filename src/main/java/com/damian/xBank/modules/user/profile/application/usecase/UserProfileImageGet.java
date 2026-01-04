package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserProfileImageGet {
    private static final Logger log = LoggerFactory.getLogger(UserProfileImageGet.class);
    private final AuthenticationContext authenticationContext;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileImageService userProfileImageService;

    public UserProfileImageGet(
            AuthenticationContext authenticationContext,
            UserAccountRepository userAccountRepository,
            UserProfileImageService userProfileImageService
    ) {
        this.authenticationContext = authenticationContext;
        this.userAccountRepository = userAccountRepository;
        this.userProfileImageService = userProfileImageService;
    }

    /**
     * It gets the user photo
     *
     * @param userId the id of the user to get the photo for
     * @return the user photo resource
     * @throws UserAccountNotFoundException      if the user does not exist
     * @throws UserProfileImageNotFoundException if the user photo does not exist in the db
     */
    public Resource execute(Long userId) {
        // find the user
        User user = userAccountRepository.findById(userId).orElseThrow(
                () -> new UserAccountNotFoundException(userId)
        );

        // check if the user has a user photo filename stored in db
        if (user.getProfile().getPhotoPath() == null) {
            throw new UserProfileImageNotFoundException(user.getProfile().getId());
        }

        log.debug("Getting user: {} user image: {}", userId, user.getProfile().getPhotoPath());

        return userProfileImageService.getImage(userId, user.getProfile().getPhotoPath());
    }

    /**
     * It gets the current user photo
     *
     * @return the current user photo resource
     */
    public Resource execute() {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        return this.execute(currentUser.getId());
    }
}
