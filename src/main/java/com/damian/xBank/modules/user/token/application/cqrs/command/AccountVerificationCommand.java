package com.damian.xBank.modules.user.token.application.cqrs.command;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @param token
 */
public record AccountVerificationCommand(
    @NotBlank
    String token
) {
}
