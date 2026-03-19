package com.damian.xBank.modules.user.profile.infrastructure.service;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.shared.infrastructure.storage.FileStorageService;
import com.damian.xBank.shared.infrastructure.storage.ImageProcessingService;
import com.damian.xBank.shared.infrastructure.storage.ImageUploaderService;
import com.damian.xBank.shared.infrastructure.storage.ImageValidationService;
import com.damian.xBank.shared.infrastructure.storage.exception.ImageTooLargeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

@Service
public class UserProfileImageService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileImageService.class);
    public static final String PROFILE_IMAGE_FOLDER = "";
    private final ImageUploaderService imageUploaderService;
    private final FileStorageService fileStorageService;
    private final ImageProcessingService imageProcessingService;
    private final ImageValidationService imageValidationService;
    private final long COMPRESS_SIZE_TRIGGER = 250L * 1024; // 250 kb
    private final long MAX_IMAGE_SIZE = 2L * 1024 * 1024; // 2 MB
    private final int MAX_WIDTH = 500; // 500px
    private final int MAX_HEIGHT = 500; // 500px
    private final String[] ALLOWED_IMAGE_TYPES = {"image/jpg", "image/jpeg", "image/png"};

    public UserProfileImageService(
        FileStorageService fileStorageService,
        ImageUploaderService imageUploaderService,
        ImageProcessingService imageProcessingService,
        ImageValidationService imageValidationService
    ) {
        this.fileStorageService = fileStorageService;
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
     * Sube una imagen al servidor
     *
     * @param image the uploaded image
     * @return image filename
     * @throws ImageTooLargeException if the image size exceeds the limit
     */
    public File uploadImage(Long userId, MultipartFile image) {
        log.debug("Uploading user: {} user image", userId);

        // run basic image validations
        imageValidationService.validateImage(
            image,
            MAX_IMAGE_SIZE,
            ALLOWED_IMAGE_TYPES
        );

        // Comprimir y redimensionar imagen
        image = imageProcessingService.optimizeImage(image, MAX_WIDTH, MAX_HEIGHT);

        // Subir imagen
        return imageUploaderService.uploadImage(
            image,
            userId,
            PROFILE_IMAGE_FOLDER,
            "avatar"
        );
    }

    /**
     * Obtiene la imagen de un usuario
     *
     * @param userId
     * @return Resource
     * @throws UserNotFoundException
     * @throws UserProfileImageNotFoundException
     */
    public Resource getImage(Long userId, String filename) {

        File file = fileStorageService.getFile(
            getProfileImageFolder(userId),
            filename
        );

        return fileStorageService.createResource(file);
    }
}
