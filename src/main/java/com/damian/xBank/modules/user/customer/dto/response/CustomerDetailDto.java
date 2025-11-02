package com.damian.xBank.modules.user.customer.dto.response;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.enums.CustomerGender;

import java.time.Instant;
import java.time.LocalDate;

public record CustomerDetailDto(
        Long id,
        UserAccountRole role,
        String email,
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
        Instant updatedAt,
        Instant createdAt
) {
}