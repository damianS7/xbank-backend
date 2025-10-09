package com.damian.xBank.modules.customer.profile;

import com.damian.xBank.modules.customer.CustomerGender;

import java.time.Instant;
import java.time.LocalDate;

public record ProfileDTO(
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
