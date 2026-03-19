package com.damian.xBank.modules.user.user.application.usecase.register;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;

import java.time.LocalDate;

/**
 * Comando para registrar un usuario.
 */
public record RegisterUserCommand(
    String email,
    String password,
    String firstName,
    String lastName,
    String phoneNumber,
    LocalDate birthdate,
    UserGender gender,
    String address,
    String zipCode,
    String country,
    String nationalId
) {
}
