package com.damian.xBank.modules.user.profile.application.usecase.get;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUserProfileImage {
    private static final Logger log = LoggerFactory.getLogger(GetCurrentUserProfileImage.class);
    private final AuthenticationContext authenticationContext;
    private final UserRepository userRepository;
    private final UserProfileImageService userProfileImageService;

    public GetCurrentUserProfileImage(
        AuthenticationContext authenticationContext,
        UserRepository userRepository,
        UserProfileImageService userProfileImageService
    ) {
        this.authenticationContext = authenticationContext;
        this.userRepository = userRepository;
        this.userProfileImageService = userProfileImageService;
    }

    /**
     * It gets the user photo
     *
     * @param query the id of the user to get the photo for
     * @return the user photo resource
     * @throws UserNotFoundException             if the user does not exist
     * @throws UserProfileImageNotFoundException if the user photo does not exist in the db
     */
    public Resource execute(GetUserProfileImageQuery query) {
        // find the user
        User user = userRepository.findById(query.userId())
            .orElseThrow(
                () -> new UserNotFoundException(query.userId())
            );

        // check if the user has a user photo filename stored in db
        if (user.getProfile().getPhotoPath() == null) {
            throw new UserProfileImageNotFoundException(user.getProfile().getId());
        }

        log.debug("Getting user: {} user image: {}", user.getId(), user.getProfile().getPhotoPath());

        return userProfileImageService.getImage(user.getId(), user.getProfile().getPhotoPath());
    }

    /**
     * It gets the current user photo
     *
     * @return the current user photo resource
     */
    public Resource execute() {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        return this.execute(new GetUserProfileImageQuery(currentUser.getId()));
    }
}
