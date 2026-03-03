package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.cqrs.command.UploadUserProfileImageCommand;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.storage.exception.ImageTooLargeException;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class UploadUserProfileImage {
    private static final Logger log = LoggerFactory.getLogger(UploadUserProfileImage.class);
    private final AuthenticationContext authenticationContext;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileImageService userProfileImageService;
    private final PasswordValidator passwordValidator;

    public UploadUserProfileImage(
        AuthenticationContext authenticationContext,
        UserProfileRepository userProfileRepository,
        UserProfileImageService userProfileImageService,
        PasswordValidator passwordValidator
    ) {
        this.authenticationContext = authenticationContext;
        this.userProfileRepository = userProfileRepository;
        this.userProfileImageService = userProfileImageService;
        this.passwordValidator = passwordValidator;
    }

    /**
     * It uploads an image and set it as user photo
     *
     * @param command the command containing the image and the current password for validation
     * @return image filename
     * @throws ImageTooLargeException if the image size exceeds the limit
     */
    public File execute(UploadUserProfileImageCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        log.debug("Uploading user: {} user image", currentUser.getId());

        // validate password
        passwordValidator.validatePassword(currentUser, command.currentPassword());

        // Upload the image
        File uploadedImage = userProfileImageService.uploadImage(
            currentUser.getId(),
            command.image()
        );

        // update user photo in db
        currentUser.getProfile().setPhotoPath(uploadedImage.getName());
        userProfileRepository.save(currentUser.getProfile());

        return uploadedImage;
    }
}
