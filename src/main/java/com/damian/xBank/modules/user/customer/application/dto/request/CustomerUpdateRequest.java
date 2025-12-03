package com.damian.xBank.modules.user.customer.application.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CustomerUpdateRequest(
        @NotBlank
        String currentPassword,

        Map<String, Object> fieldsToUpdate
) {
}
