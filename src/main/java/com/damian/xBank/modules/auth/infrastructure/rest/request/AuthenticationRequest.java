package com.damian.xBank.modules.auth.infrastructure.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request para iniciar sesión.
 */
public record AuthenticationRequest(
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    String email,

    @NotBlank(message = "{validation.password.required}")
    String password
) {
}
