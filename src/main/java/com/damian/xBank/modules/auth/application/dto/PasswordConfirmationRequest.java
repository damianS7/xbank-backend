package com.damian.xBank.modules.auth.application.dto;

import jakarta.validation.constraints.NotNull;

public record PasswordConfirmationRequest(
        @NotNull(
                message = "Password must not be null"
        )
        String password
) {
}
