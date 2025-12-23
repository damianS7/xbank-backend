package com.damian.xBank.infrastructure.storage;

import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service class for handling image uploads to the server.
 */
@Service
public class ImageUploaderService {
    public static final String UPLOAD_PATH = "uploads/images/users/{userId}";
    private static final Logger log = LoggerFactory.getLogger(ImageUploaderService.class);
    private final FileStorageService fileStorageService;
    private final AuthenticationContext authenticationContext;

    public ImageUploaderService(
            FileStorageService fileStorageService,
            AuthenticationContext authenticationContext
    ) {
        this.fileStorageService = fileStorageService;
        this.authenticationContext = authenticationContext;
    }

    public static String getUserUploadFolder(Long userId) {
        return UPLOAD_PATH.replace("{userId}", userId.toString());
    }

    /**
     * Uploads an image to the server
     */
    public File uploadImage(MultipartFile file, String folder, String filename) {
        final User currentUser = authenticationContext.getCurrentUser();
        Path path = Paths.get(
                getUserUploadFolder(currentUser.getId()),
                folder
        );

        final String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension != null && !filename.endsWith(extension)) {
            filename += "." + extension;
        }

        log.debug("user: {} uploading file: {} to: {}", currentUser.getId(), filename, path);
        // saving file
        return fileStorageService.storeFile(file, path.toString(), filename);
    }

    /**
     * Uploads an image to the server
     */
    public File uploadImage(MultipartFile file, String folder) {
        String filename = UUID.randomUUID().toString();
        return this.uploadImage(file, folder, filename);
    }
}
