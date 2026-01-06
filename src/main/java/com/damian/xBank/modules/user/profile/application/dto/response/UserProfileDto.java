package com.damian.xBank.modules.user.profile.application.dto.response;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;

import java.time.Instant;
import java.time.LocalDate;

public record UserProfileDto(
        Long id,
        String firstName,
        String lastName,
        String phone,
        LocalDate birthdate,
        UserGender gender,
        String photoPath,
        String address,
        String postalCode,
        String country,
        String nationalId,
        Instant updatedAt
) {
}