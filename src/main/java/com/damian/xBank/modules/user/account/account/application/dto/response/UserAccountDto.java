package com.damian.xBank.modules.user.account.account.application.dto.response;

import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;

import java.time.Instant;

public record UserAccountDto(
        Long id,
        String email,
        UserAccountRole role,
        Instant createdAt
) {
}