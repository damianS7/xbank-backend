package com.damian.whatsapp.modules.user.user.service;

import com.damian.whatsapp.modules.user.user.exception.UserImageNotFoundException;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.infrastructure.storage.FileStorageService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageProcessingService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageUploaderService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageValidationService;
import com.damian.whatsapp.shared.infrastructure.storage.exception.ImageTooLargeException;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

@Service
public class UserImageService {

    public static final String PROFILE_IMAGE_FOLDER = "";
    private static final Logger log = LoggerFactory.getLogger(UserImageService.class);
    private final UserRepository userRepository;
    private final ImageUploaderService imageUploaderService;
    private final FileStorageService fileStorageService;
    private final ImageProcessingService imageProcessingService;
    private final ImageValidationService imageValidationService;
    private final long COMPRESS_SIZE_TRIGGER = 250L * 1024; // 250 kb
    private final long MAX_IMAGE_SIZE = 2L * 1024 * 1024; // 2 MB
    private final int MAX_WIDTH = 500; // 500px
    private final int MAX_HEIGHT = 500; // 500px
    private final String[] ALLOWED_IMAGE_TYPES = {"image/jpg", "image/jpeg", "image/png"};

    public UserImageService(
            FileStorageService fileStorageService,
            UserRepository userRepository,
            ImageUploaderService imageUploaderService,
            ImageProcessingService imageProcessingService,
            ImageValidationService imageValidationService
    ) {
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.imageUploaderService = imageUploaderService;
        this.imageProcessingService = imageProcessingService;
        this.imageValidationService = imageValidationService;
    }

    public String getProfileImageFolder(Long userId) {
        return Paths.get(
                ImageUploaderService.getUserUploadFolder(userId),
                PROFILE_IMAGE_FOLDER
        ).toString();
    }

    /**
     * It uploads an image and set it as user photo
     *
     * @param currentPassword the password of the current user
     * @param image           the uploaded image
     * @return image filename
     * @throws ImageTooLargeException if the image size exceeds the limit
     */
    public File uploadUserImage(String currentPassword, MultipartFile image) {
        final User currentUser = AuthHelper.getLoggedUser();
        log.debug("Uploading user: {} user image", currentUser.getId());

        // validate password
        AuthHelper.validatePassword(currentUser, currentPassword);

        // run basic image validations
        imageValidationService.validateImage(
                image,
                MAX_IMAGE_SIZE,
                ALLOWED_IMAGE_TYPES
        );

        // At this point the image is guaranteed to be not null and of an allowed type
        // Image optimizations (resize and compress)
        image = imageProcessingService.optimizeImage(image, MAX_WIDTH, MAX_HEIGHT);

        // Upload the image
        File uploadedImage = imageUploaderService.uploadImage(
                image,
                PROFILE_IMAGE_FOLDER,
                "avatar"
        );

        // update user photo in db
        currentUser.setImageFilename(uploadedImage.getName());
        userRepository.save(currentUser);

        return uploadedImage;
    }

    /**
     * It gets the user photo
     *
     * @param userId the id of the user to get the photo for
     * @return the user photo resource
     * @throws UserNotFoundException      if the user does not exist
     * @throws UserImageNotFoundException if the user photo does not exist in the db
     */
    public Resource getUserImage(Long userId) {
        // find the user
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(Exceptions.USER.IMAGE.NOT_FOUND, userId)
        );

        // check if the user has a user photo filename stored in db
        if (user.getImageFilename() == null) {
            throw new UserImageNotFoundException(
                    Exceptions.USER.IMAGE.NOT_FOUND,
                    user.getId()
            );
        }

        log.debug("Getting user: {} user image: {}", userId, user.getImageFilename());

        File file = fileStorageService.getFile(
                getProfileImageFolder(userId),
                user.getImageFilename()
        );

        // return the image as resource
        return fileStorageService.createResource(file);
    }

    /**
     * It gets the current user photo
     *
     * @return the current user photo resource
     */
    public Resource getUserImage() {
        final User currentUser = AuthHelper.getLoggedUser();

        return this.getUserImage(currentUser.getId());
    }
}
