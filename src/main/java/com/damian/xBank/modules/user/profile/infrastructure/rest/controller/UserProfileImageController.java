package com.damian.xBank.modules.user.profile.infrastructure.rest.controller;

import com.damian.xBank.modules.user.profile.application.cqrs.command.UploadUserProfileImageCommand;
import com.damian.xBank.modules.user.profile.application.cqrs.query.GetUserProfileImageQuery;
import com.damian.xBank.modules.user.profile.application.usecase.GetCurrentUserProfileImage;
import com.damian.xBank.modules.user.profile.application.usecase.UploadUserProfileImage;
import com.damian.xBank.shared.utils.ImageHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RequestMapping("/api/v1")
@RestController
public class UserProfileImageController {
    private final GetCurrentUserProfileImage getCurrentUserProfileImage;
    private final UploadUserProfileImage uploadUserProfileImage;

    @Autowired
    public UserProfileImageController(
        GetCurrentUserProfileImage getCurrentUserProfileImage,
        UploadUserProfileImage uploadUserProfileImage
    ) {
        this.getCurrentUserProfileImage = getCurrentUserProfileImage;
        this.uploadUserProfileImage = uploadUserProfileImage;
    }

    // endpoint to get the current customer profile image
    @GetMapping("/profiles/{userId}/image")
    public ResponseEntity<?> getProfileImage(
        @PathVariable @NotNull @Positive
        Long userId
    ) {
        GetUserProfileImageQuery query = new GetUserProfileImageQuery(userId);
        Resource resource = getCurrentUserProfileImage.execute(query);
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.parseMediaType(contentType))
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
            .body(resource);
    }

    // endpoint to get the current user profile image
    @GetMapping("/profiles/image")
    public ResponseEntity<?> getProfileImage() {
        Resource resource = getCurrentUserProfileImage.execute();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.parseMediaType(contentType))
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
            .body(resource);
    }

    // endpoint for the current user to upload his profile photo
    @PostMapping("/profiles/image")
    public ResponseEntity<?> uploadProfileImage(
        @RequestParam("currentPassword") @NotBlank
        String currentPassword,
        @RequestParam("file") MultipartFile file
    ) {
        UploadUserProfileImageCommand command = new UploadUserProfileImageCommand(
            currentPassword, file
        );
        uploadUserProfileImage.execute(command);
        Resource resource = getCurrentUserProfileImage.execute();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }
}

