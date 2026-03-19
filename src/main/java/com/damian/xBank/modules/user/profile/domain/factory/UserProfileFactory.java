package com.damian.xBank.modules.user.profile.domain.factory;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.application.usecase.register.RegisterUserCommand;
import com.damian.xBank.modules.user.user.infrastructure.rest.request.RegisterUserRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserProfileFactory {
    public static UserProfile defaultProfile() {
        return UserProfile.create(
            "", "", "", LocalDate.now(), "avatar.jpg",
            "", "", "", "", UserGender.MALE
        );
    }

    public static UserProfile testProfile() {
        return UserProfile.create(null)
            .setNationalId("123456789Z")
            .setFirstName("David")
            .setLastName("Brow")
            .setBirthdate(LocalDate.now())
            .setPhotoPath("avatar.jpg")
            .setPhoneNumber("123 123 123")
            .setPostalCode("01003")
            .setAddress("Fake ave")
            .setCountry("US")
            .setGender(UserGender.MALE);
    }

    public UserProfile create(RegisterUserRequest request) {
        return UserProfile.create(null)
            .setNationalId(request.nationalId())
            .setFirstName(request.firstName())
            .setLastName(request.lastName())
            .setPhoneNumber(request.phoneNumber())
            .setGender(request.gender())
            .setBirthdate(request.birthdate())
            .setCountry(request.country())
            .setAddress(request.address())
            .setPostalCode((request.zipCode()))
            .setPhotoPath("avatar.jpg");
    }

    public UserProfile create(RegisterUserCommand command) {
        return UserProfile.create(null)
            .setNationalId(command.nationalId())
            .setFirstName(command.firstName())
            .setLastName(command.lastName())
            .setPhoneNumber(command.phoneNumber())
            .setGender(command.gender())
            .setBirthdate(command.birthdate())
            .setCountry(command.country())
            .setAddress(command.address())
            .setPostalCode((command.zipCode()))
            .setPhotoPath("avatar.jpg");
    }

}