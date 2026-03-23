package com.damian.xBank.modules.user.user.application.dto;

import com.damian.xBank.modules.user.profile.application.dto.UserProfileResult;
import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserDetailResult(
    Long id,
    String email,
    UserRole role,
    UserProfileResult profile,
    Instant createdAt
) {
}