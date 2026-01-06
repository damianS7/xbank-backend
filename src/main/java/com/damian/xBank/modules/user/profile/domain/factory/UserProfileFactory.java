package com.damian.xBank.modules.user.profile.domain.factory;

import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.application.dto.request.UserRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
public class UserProfileFactory {

    // TODO createFrom?
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