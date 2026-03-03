package com.damian.xBank.modules.auth.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;

public record PasswordConfirmationRequest(
    @NotNull(
        message = "Password must not be null"
    )
    String password
) {
}
