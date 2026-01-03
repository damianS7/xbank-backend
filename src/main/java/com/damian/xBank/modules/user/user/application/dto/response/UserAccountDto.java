package com.damian.xBank.modules.user.user.application.dto.response;

import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;

import java.time.Instant;

public record UserAccountDto(
        Long id,
        String email,
        UserAccountRole role,
        Instant createdAt
) {
}