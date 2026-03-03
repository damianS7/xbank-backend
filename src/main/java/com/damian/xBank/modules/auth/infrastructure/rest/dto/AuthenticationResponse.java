package com.damian.xBank.modules.auth.infrastructure.rest.dto;

/**
 * Used for returning the token after successful authentication.
 *
 * @param token jwt token
 */
public record AuthenticationResponse(
    String token
) {
}

