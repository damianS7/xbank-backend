package com.damian.xBank.modules.user.profile.application.dto.response;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;

import java.time.Instant;
import java.time.LocalDate;

public record UserProfileDetailDto(
        Long id,
        UserAccountRole role,
        String email,
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
        Instant updatedAt,
        Instant createdAt
) {
}