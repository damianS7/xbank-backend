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

/**
 * Caso de uso para obtener la imagen de perfil del usuario actual.
 */
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
     * @param query La consulta con el id del usuario
     * @return Resource
     * @throws UserNotFoundException             if the user does not exist
     * @throws UserProfileImageNotFoundException if the user photo does not exist in the db
     */
    public Resource execute(GetUserProfileImageQuery query) {
        // Buscar el usuario por ID
        User user = userRepository.findById(query.userId())
            .orElseThrow(
                () -> new UserNotFoundException(query.userId())
            );

        // Comprobar que tenga un path almacenado
        if (user.getProfile().getPhotoPath() == null) {
            throw new UserProfileImageNotFoundException(user.getProfile().getId());
        }

        log.debug("Getting user: {} user image: {}", user.getId(), user.getProfile().getPhotoPath());

        return userProfileImageService.getImage(user.getId(), user.getProfile().getPhotoPath());
    }

    /**
     * @return Resource
     */
    public Resource execute() {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        return this.execute(new GetUserProfileImageQuery(currentUser.getId()));
    }
}
