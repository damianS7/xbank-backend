package com.damian.xBank.modules.user.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UserAccountUpdateRequest(
        @NotBlank
        String currentPassword,

        @NotNull(message = "You must send at least one field to update")
        @NotEmpty(message = "You must send at least one field to update")
        Map<
                @NotBlank(message = "Field cannot be empty")
                        String,
                @NotNull(message = "Field value cannot be null")
                        Object> fieldsToUpdate
) {
}
