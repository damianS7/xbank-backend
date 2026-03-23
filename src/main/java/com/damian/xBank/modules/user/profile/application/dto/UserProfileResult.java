package com.damian.xBank.modules.user.profile.application.dto;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;

import java.time.Instant;
import java.time.LocalDate;

public record UserProfileResult(
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
    public static UserProfileResult from(UserProfile userProfile) {
        return new UserProfileResult(
            userProfile.getId(),
            userProfile.getFirstName(),
            userProfile.getLastName(),
            userProfile.getPhoneNumber(),
            userProfile.getBirthdate(),
            userProfile.getGender(),
            userProfile.getPhotoPath(),
            userProfile.getAddress(),
            userProfile.getPostalCode(),
            userProfile.getCountry(),
            userProfile.getNationalId(),
            userProfile.getUpdatedAt()
        );
    }
}