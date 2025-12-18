package com.damian.xBank.modules.user.customer.application.service;

import com.damian.xBank.infrastructure.storage.FileStorageService;
import com.damian.xBank.infrastructure.storage.ImageProcessingService;
import com.damian.xBank.infrastructure.storage.ImageUploaderService;
import com.damian.xBank.infrastructure.storage.ImageValidationService;
import com.damian.xBank.infrastructure.storage.exception.ImageTooLargeException;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerImageNotFoundException;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

@Service
public class CustomerImageService {

    public static final String PROFILE_IMAGE_FOLDER = "";
    private static final Logger log = LoggerFactory.getLogger(CustomerImageService.class);
    private final UserAccountRepository userAccountRepository;
    private final ImageUploaderService imageUploaderService;
    private final FileStorageService fileStorageService;
    private final PasswordValidator passwordValidator;
    private final ImageProcessingService imageProcessingService;
    private final ImageValidationService imageValidationService;
    private final long COMPRESS_SIZE_TRIGGER = 250L * 1024; // 250 kb
    private final long MAX_IMAGE_SIZE = 2L * 1024 * 1024; // 2 MB
    private final int MAX_WIDTH = 500; // 500px
    private final int MAX_HEIGHT = 500; // 500px
    private final String[] ALLOWED_IMAGE_TYPES = {"image/jpg", "image/jpeg", "image/png"};
    private final AuthenticationContext authenticationContext;

    public CustomerImageService(
            FileStorageService fileStorageService,
            UserAccountRepository userAccountRepository,
            ImageUploaderService imageUploaderService, PasswordValidator passwordValidator,
            ImageProcessingService imageProcessingService,
            ImageValidationService imageValidationService,
            AuthenticationContext authenticationContext
    ) {
        this.fileStorageService = fileStorageService;
        this.userAccountRepository = userAccountRepository;
        this.imageUploaderService = imageUploaderService;
        this.passwordValidator = passwordValidator;
        this.imageProcessingService = imageProcessingService;
        this.imageValidationService = imageValidationService;
        this.authenticationContext = authenticationContext;
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
        final User currentUser = authenticationContext.getCurrentUser();
        log.debug("Uploading user: {} user image", currentUser.getId());

        // validate password
        passwordValidator.validatePassword(currentUser, currentPassword);

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
        currentUser.getCustomer().setPhotoPath(uploadedImage.getName());
        userAccountRepository.save(currentUser.getAccount());

        return uploadedImage;
    }

    /**
     * It gets the user photo
     *
     * @param userId the id of the user to get the photo for
     * @return the user photo resource
     * @throws UserAccountNotFoundException   if the user does not exist
     * @throws CustomerImageNotFoundException if the user photo does not exist in the db
     */
    public Resource getUserImage(Long userId) {
        // find the user
        UserAccount user = userAccountRepository.findById(userId).orElseThrow(
                () -> new UserAccountNotFoundException(userId)
        );

        // check if the user has a user photo filename stored in db
        if (user.getCustomer().getPhotoPath() == null) {
            throw new CustomerImageNotFoundException(user.getId()); // TODO user.getCustomer.getId
        }

        log.debug("Getting user: {} user image: {}", userId, user.getCustomer().getPhotoPath());

        File file = fileStorageService.getFile(
                getProfileImageFolder(userId),
                user.getCustomer().getPhotoPath()
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
        final User currentUser = authenticationContext.getCurrentUser();

        return this.getUserImage(currentUser.getId());
    }
}
