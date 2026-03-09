package com.damian.xBank.modules.user.user.application.dto;

import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserResult(
    Long id,
    String email,
    UserRole role,
    Instant createdAt
) {
}