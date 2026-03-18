package com.damian.xBank.modules.user.profile.application.usecase.update;

import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.infrastructure.storage.exception.ImageTooLargeException;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Caso de uso para actualizar la imagen de perfil del usuario actual.
 */
@Service
public class UploadUserProfileImage {
    private static final Logger log = LoggerFactory.getLogger(UploadUserProfileImage.class);
    private final AuthenticationContext authenticationContext;
    private final UserRepository userRepository;
    private final UserProfileImageService userProfileImageService;
    private final PasswordValidator passwordValidator;

    public UploadUserProfileImage(
        AuthenticationContext authenticationContext,
        UserRepository userRepository,
        UserProfileImageService userProfileImageService,
        PasswordValidator passwordValidator
    ) {
        this.authenticationContext = authenticationContext;
        this.userRepository = userRepository;
        this.userProfileImageService = userProfileImageService;
        this.passwordValidator = passwordValidator;
    }

    /**
     * @param command Comando con los datos necesarios
     * @return File
     * @throws ImageTooLargeException
     */
    public File execute(UploadUserProfileImageCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        log.debug("Uploading user: {} user image", currentUser.getId());

        // Validar password
        passwordValidator.validatePassword(currentUser, command.currentPassword());

        // Upload the image
        File uploadedImage = userProfileImageService.uploadImage(
            currentUser.getId(),
            command.image()
        );

        currentUser.getProfile().setPhotoPath(uploadedImage.getName());
        userRepository.save(currentUser);

        return uploadedImage;
    }
}
