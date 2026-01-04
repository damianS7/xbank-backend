package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;

import java.time.LocalDate;

public class UserProfileTestFactory {
    private UserProfileTestFactory() {
    }

    public static UserProfile aProfile() {
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
}