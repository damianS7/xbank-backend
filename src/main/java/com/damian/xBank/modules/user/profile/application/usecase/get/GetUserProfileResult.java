package com.damian.xBank.modules.user.profile.application.usecase.get;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;

import java.time.Instant;
import java.time.LocalDate;

public record GetUserProfileResult(
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
    public static GetUserProfileResult from(UserProfile userProfile) {
        return new GetUserProfileResult(
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