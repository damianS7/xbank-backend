package com.damian.xBank.modules.customer.dto;

import com.damian.xBank.modules.customer.CustomerRole;
import com.damian.xBank.modules.customer.profile.ProfileDTO;

import java.time.Instant;

public record CustomerWithProfileDTO(
        Long id,
        String email,
        CustomerRole role,
        ProfileDTO profile,
        Instant createdAt,
        Instant updatedAt
) {
}