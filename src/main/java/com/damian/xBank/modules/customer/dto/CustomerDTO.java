package com.damian.xBank.modules.customer.dto;

import com.damian.xBank.modules.customer.CustomerRole;

import java.time.Instant;

public record CustomerDTO(
        Long id,
        String email,
        CustomerRole role,
        Instant createdAt,
        Instant updatedAt
) {
}