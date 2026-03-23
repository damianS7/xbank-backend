package com.damian.xBank.modules.user.profile.application.usecase.update;

import org.springframework.web.multipart.MultipartFile;

public record UploadUserProfileImageCommand(
    String currentPassword,
    MultipartFile image
) {
}
