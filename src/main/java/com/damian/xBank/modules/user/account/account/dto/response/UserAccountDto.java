package com.damian.xBank.modules.user.account.account.dto.response;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;

import java.time.Instant;

public record UserAccountDto(
        Long id,
        String email,
        UserAccountRole role,
        Instant createdAt
) {
}