package com.damian.xBank.modules.auth.infrastructure.rest.response;

/**
 * Response para AuthenticationRequest
 *
 * @param token jwt token
 *
 */
public record AuthenticationResponse(
    String token
) {
}

