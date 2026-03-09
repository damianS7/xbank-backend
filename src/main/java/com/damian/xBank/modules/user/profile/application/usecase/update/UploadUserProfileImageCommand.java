package com.damian.xBank.modules.user.profile.application.usecase.update;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record UploadUserProfileImageCommand(
    @NotBlank
    String currentPassword,
    MultipartFile image
) {
}
