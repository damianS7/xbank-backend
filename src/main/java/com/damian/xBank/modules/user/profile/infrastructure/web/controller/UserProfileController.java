package com.damian.xBank.modules.user.profile.infrastructure.web.controller;

import com.damian.xBank.modules.user.profile.application.dto.mapper.UserProfileDtoMapper;
import com.damian.xBank.modules.user.profile.application.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDetailDto;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileGet;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileUpdate;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RequestMapping("/api/v1")
@RestController
public class UserProfileController {
    private final UserProfileGet userProfileGet;
    private final UserProfileUpdate userProfileUpdate;
    private final UserProfileImageService userProfileImageService;

    @Autowired
    public UserProfileController(
            UserProfileGet userProfileGet,
            UserProfileUpdate userProfileUpdate,
            UserProfileImageService userProfileImageService
    ) {
        this.userProfileGet = userProfileGet;
        this.userProfileUpdate = userProfileUpdate;
        this.userProfileImageService = userProfileImageService;
    }

    // endpoint to receive current customer
    @GetMapping("/profiles")
    public ResponseEntity<?> getLoggedCustomerData() {
        UserProfile customer = userProfileGet.execute();
        UserProfileDetailDto dto = UserProfileDtoMapper.toCustomerWithAccountDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    // endpoint to modify current customer profile
    @PatchMapping("/profiles")
    public ResponseEntity<UserProfileDetailDto> update(
            @Validated @RequestBody
            UserProfileUpdateRequest request
    ) {
        UserProfile customer = userProfileUpdate.execute(request);
        //        CustomerDto customerDto = CustomerDtoMapper.toCustomerDto(customer);
        UserProfileDetailDto customerDto = UserProfileDtoMapper.toCustomerWithAccountDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDto);
    }

    // endpoint to get the current customer profile image
    @GetMapping("/profiles/{userId}/image")
    public ResponseEntity<?> getProfileImage(
            @PathVariable @NotNull @Positive
            Long userId
    ) {
        Resource resource = userProfileImageService.getImage(userId);
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
        Resource resource = userProfileImageService.getImage();
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
        userProfileImageService.uploadImage(currentPassword, file);
        Resource resource = userProfileImageService.getImage();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}

