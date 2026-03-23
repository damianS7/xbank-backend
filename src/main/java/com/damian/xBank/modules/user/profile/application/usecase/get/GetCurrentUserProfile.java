package com.damian.xBank.modules.user.profile.application.usecase.get;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para obtener el perfil del usuario actual.
 */
@Service
public class GetCurrentUserProfile {
    private static final Logger log = LoggerFactory.getLogger(GetCurrentUserProfile.class);
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public GetCurrentUserProfile(
        UserRepository userRepository,
        AuthenticationContext authenticationContext
    ) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     *
     * @return GetUserProfileResult
     * @throws UserProfileNotFoundException
     */
    public GetUserProfileResult execute(GetUserProfileQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Buscar el usuario
        User user = userRepository
            .findById(currentUser.getId())
            .orElseThrow(
                () -> new UserProfileNotFoundException(currentUser.getId())
            );

        return GetUserProfileResult.from(user.getProfile());
    }
}
