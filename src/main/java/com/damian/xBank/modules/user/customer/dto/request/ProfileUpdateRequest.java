package com.damian.xBank.modules.user.customer.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record ProfileUpdateRequest(
        @NotBlank
        String currentPassword,

        Map<String, Object> fieldsToUpdate
) {
}
