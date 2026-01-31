package com.damian.xBank.modules.user.user.application.dto.response;

import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDto;
import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserDetailDto(
        Long id,
        String email,
        UserRole role,
        UserProfileDto profile,
        Instant createdAt
) {
}