package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.storage.exception.ImageTooLargeException;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class UserProfileImageUpload {
    private static final Logger log = LoggerFactory.getLogger(UserProfileImageUpload.class);
    private final AuthenticationContext authenticationContext;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileImageService userProfileImageService;
    private final PasswordValidator passwordValidator;

    public UserProfileImageUpload(
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
     * @param currentPassword the password of the current user
     * @param image           the uploaded image
     * @return image filename
     * @throws ImageTooLargeException if the image size exceeds the limit
     */
    public File execute(String currentPassword, MultipartFile image) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        log.debug("Uploading user: {} user image", currentUser.getId());

        // validate password
        passwordValidator.validatePassword(currentUser, currentPassword);

        // Upload the image
        File uploadedImage = userProfileImageService.uploadImage(
                currentUser.getId(),
                image
        );

        // update user photo in db
        currentUser.getProfile().setPhotoPath(uploadedImage.getName());
        userProfileRepository.save(currentUser.getProfile());

        return uploadedImage;
    }
}
