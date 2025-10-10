package com.damian.whatsapp.modules.user.user.dto.response;

import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;

import java.time.Instant;
import java.time.LocalDate;

public record UserDto(
        Long id,
        String email,
        UserRole role,
        String userName,
        String firstName,
        String lastName,
        String phone,
        LocalDate birthdate,
        UserGender gender,
        String avatarFilename,
        Instant createdAt
) {
}