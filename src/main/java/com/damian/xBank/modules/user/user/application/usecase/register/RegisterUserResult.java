package com.damian.xBank.modules.user.user.application.usecase.register;

import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record RegisterUserResult(
    Long id,
    String email,
    UserRole role,
    Instant createdAt
) {
}