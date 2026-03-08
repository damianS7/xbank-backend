package com.damian.xBank.modules.auth.infrastructure.rest.response;

/**
 * Used for returning the token after successful authentication.
 *
 * @param token jwt token
 */
public record AuthenticationResponse(
    String token
) {
}

