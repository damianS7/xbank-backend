package com.damian.xBank.modules.user.profile.application.usecase.update;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileUpdateException;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Caso de uso para modificar el perfil del usuario actual
 */
@Service
public class UpdateCurrentUserProfile {
    private static final Logger log = LoggerFactory.getLogger(UpdateCurrentUserProfile.class);
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public UpdateCurrentUserProfile(
        UserRepository userRepository,
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator
    ) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    public UpdateUserProfileResult execute(UpdateUserProfileCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        return execute(currentUser.getId(), command);
    }

    /**
     *
     * @param userId  ID del usuario cuyo perfil se actualizará
     * @param command El comando con los datos
     * @return UpdateUserProfileResult
     * @throws UserProfileNotFoundException
     */
    public UpdateUserProfileResult execute(Long userId, UpdateUserProfileCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Buscar el usuario del que queremos modificar el perfil
        User user = userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UserProfileNotFoundException(userId)
            );

        // Si no es un admin
        if (!currentUser.isAdmin()) {
            // Validar password
            passwordValidator.validatePassword(currentUser, command.currentPassword());
        }

        // we iterate over the fields (if any)
        command.fieldsToUpdate().forEach((key, value) -> {
            switch (key) {
                case "firstName" -> user.getProfile().setFirstName((String) value);
                case "lastName" -> user.getProfile().setLastName((String) value);
                case "phoneNumber" -> user.getProfile().setPhoneNumber((String) value);
                case "country" -> user.getProfile().setCountry((String) value);
                case "zipCode" -> user.getProfile().setPostalCode((String) value);
                case "address" -> user.getProfile().setAddress((String) value);
                case "photo" -> user.getProfile().setPhotoPath((String) value);
                case "gender" -> user.getProfile().setGender(UserGender.valueOf((String) value));
                case "birthdate" -> user.getProfile().setBirthdate(LocalDate.parse((String) value));
                default -> throw new UserProfileUpdateException(
                    userId, new Object[]{key, value.toString()}
                );
            }
        });

        // Guardar cambios
        userRepository.save(user);

        return UpdateUserProfileResult.from(user.getProfile());
    }
}
