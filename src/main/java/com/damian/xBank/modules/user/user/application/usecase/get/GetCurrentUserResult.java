package com.damian.xBank.modules.user.user.application.usecase.get;

import com.damian.xBank.modules.user.profile.application.dto.UserProfileResult;
import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record GetCurrentUserResult(
    Long id,
    String email,
    UserRole role,
    UserProfileResult profile,
    Instant createdAt
) {
}