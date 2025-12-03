package com.damian.xBank.modules.user.account.account.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request again the email with the token activation.
 *
 * @param email
 */
public record UserAccountVerificationResendRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a well-formed email address.")
        String email
) {
}
