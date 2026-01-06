package com.damian.xBank.modules.user.profile.domain.factory;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.application.dto.request.UserRegistrationRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserProfileFactory {
    public static UserProfile testProfile() {
        return UserProfile.create()
                          .setNationalId("123456789Z")
                          .setFirstName("David")
                          .setLastName("Brow")
                          .setBirthdate(LocalDate.now())
                          .setPhotoPath("avatar.jpg")
                          .setPhone("123 123 123")
                          .setPostalCode("01003")
                          .setAddress("Fake ave")
                          .setCountry("US")
                          .setGender(UserGender.MALE);
    }

    public UserProfile create(UserRegistrationRequest request) {
        return UserProfile.create()
                          .setNationalId(request.nationalId())
                          .setFirstName(request.firstName())
                          .setLastName(request.lastName())
                          .setPhone(request.phoneNumber())
                          .setGender(request.gender())
                          .setBirthdate(request.birthdate())
                          .setCountry(request.country())
                          .setAddress(request.address())
                          .setPostalCode((request.zipCode()))
                          .setPhotoPath("avatar.jpg");
    }

}