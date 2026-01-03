package com.damian.xBank.modules.user.user.application.dto.response;

import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;

import java.time.Instant;

public record UserAccountDetailDto(
        Long id,
        String email,
        UserAccountRole role,
        UserProfile profile,
        Instant createdAt
) {
}