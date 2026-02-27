package com.damian.xBank.modules.user.user.application.cqrs.command;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;

import java.time.LocalDate;

/**
 * Contains all the data required for Customer registration
 */
public record UserRegistrationCommand(
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
