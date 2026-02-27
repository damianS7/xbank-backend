package com.damian.xBank.modules.user.user.application.cqrs.result;

import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserRegistrationResult(
    Long id,
    String email,
    UserRole role,
    Instant createdAt
) {
}