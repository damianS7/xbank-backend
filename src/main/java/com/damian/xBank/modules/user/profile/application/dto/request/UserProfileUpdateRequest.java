package com.damian.xBank.modules.user.profile.application.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record UserProfileUpdateRequest(
        @NotBlank
        String currentPassword,

        Map<String, Object> fieldsToUpdate
) {
}
