package com.damian.xBank.modules.user.customer.application.dto.response;

import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;

import java.time.Instant;
import java.time.LocalDate;

public record CustomerDto(
        Long id,
        String firstName,
        String lastName,
        String phone,
        LocalDate birthdate,
        CustomerGender gender,
        String photoPath,
        String address,
        String postalCode,
        String country,
        String nationalId,
        Instant updatedAt
) {
}