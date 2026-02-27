package com.damian.xBank.modules.user.user.application.cqrs.result;

import com.damian.xBank.modules.user.profile.infrastructure.rest.dto.response.UserProfileDto;
import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserDetailResult(
    Long id,
    String email,
    UserRole role,
    UserProfileDto profile,
    Instant createdAt
) {
}