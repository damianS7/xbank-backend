package com.damian.xBank.modules.user.user.application.dto.response;

import com.damian.xBank.modules.user.user.domain.model.UserRole;

import java.time.Instant;

public record UserDto(
        Long id,
        String email,
        UserRole role,
        Instant createdAt
) {
}