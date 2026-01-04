package com.damian.xBank.modules.user.user.application.dto.response;

import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserAccountDetailDto(
        Long id,
        String email,
        UserRole role,
        UserProfile profile,
        Instant createdAt
) {
}