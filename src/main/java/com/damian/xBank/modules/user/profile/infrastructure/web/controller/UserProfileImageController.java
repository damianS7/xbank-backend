package com.damian.xBank.modules.user.profile.infrastructure.web.controller;

import com.damian.xBank.modules.user.profile.application.usecase.UserProfileGet;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileImageGet;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileImageUpload;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileUpdate;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RequestMapping("/api/v1")
@RestController
public class UserProfileImageController {
    private final UserProfileGet userProfileGet;
    private final UserProfileUpdate userProfileUpdate;
    private final UserProfileImageGet userProfileImageGet;
    private final UserProfileImageUpload userProfileImageUpload;

    @Autowired
    public UserProfileImageController(
            UserProfileGet userProfileGet,
            UserProfileUpdate userProfileUpdate,
            UserProfileImageGet userProfileImageGet,
            UserProfileImageUpload userProfileImageUpload
    ) {
        this.userProfileGet = userProfileGet;
        this.userProfileUpdate = userProfileUpdate;
        this.userProfileImageGet = userProfileImageGet;
        this.userProfileImageUpload = userProfileImageUpload;
    }

    // endpoint to get the current customer profile image
    @GetMapping("/profiles/{userId}/image")
    public ResponseEntity<?> getProfileImage(
            @PathVariable @NotNull @Positive
            Long userId
    ) {
        Resource resource = userProfileImageGet.execute(userId);
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
        Resource resource = userProfileImageGet.execute();
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
        userProfileImageUpload.execute(currentPassword, file);
        Resource resource = userProfileImageGet.execute();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}

