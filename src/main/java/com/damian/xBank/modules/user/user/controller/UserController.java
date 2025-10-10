package com.damian.whatsapp.modules.user.user.controller;

import com.damian.whatsapp.modules.user.user.dto.mapper.UserDtoMapper;
import com.damian.whatsapp.modules.user.user.dto.request.UserUpdateRequest;
import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.modules.user.user.service.UserImageService;
import com.damian.whatsapp.modules.user.user.service.UserService;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.util.ImageHelper;
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
public class UserController {
    private final UserService userService;
    private final UserImageService userImageService;

    @Autowired
    public UserController(
            UserService userService,
            UserImageService userImageService
    ) {
        this.userService = userService;
        this.userImageService = userImageService;
    }

    // endpoint to receive current user data
    @GetMapping("/users")
    public ResponseEntity<?> getUser() {
        User user = userService.getUser();
        UserDto dto = UserDtoMapper.toUserDto(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    // endpoint to modify the logged user data
    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(
            @Validated @RequestBody
            UserUpdateRequest request
    ) {
        User user = userService.updateUser(request);
        UserDto userDto = UserDtoMapper.toUserDto(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    // endpoint to get the current user profile image
    @GetMapping("/users/{userId}/image")
    public ResponseEntity<?> getProfileImage(
            @PathVariable @NotNull @Positive
            Long userId
    ) {
        Resource resource = userImageService.getUserImage(userId);
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(resource);
    }

    // endpoint to get the current user profile image
    @GetMapping("/users/image")
    public ResponseEntity<?> getProfileImage() {
        Resource resource = userImageService.getUserImage();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(resource);
    }

    // endpoint for the current user to upload his profile photo
    @PostMapping("/users/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("currentPassword") @NotBlank
            String currentPassword,
            @RequestParam("file") MultipartFile file
    ) {
        userImageService.uploadUserImage(currentPassword, file);
        Resource resource = userImageService.getUserImage();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


}

