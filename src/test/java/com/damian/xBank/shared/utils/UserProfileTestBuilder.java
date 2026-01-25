package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;

import java.time.LocalDate;

public class UserProfileTestBuilder {

    private Long id = null;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthdate;
    private String photo;
    private String address;
    private String postalCode;
    private String country;
    private String nationalId;
    private UserGender gender;

    private UserProfileTestBuilder() {
    }

    // Builder
    public static UserProfileTestBuilder defaultProfile() {
        UserProfileTestBuilder builder = new UserProfileTestBuilder();

        builder.firstName = "Demo";
        builder.lastName = "User";
        builder.phoneNumber = "+34123456789";
        builder.birthdate = LocalDate.of(1990, 1, 1);
        builder.photo = "avatar.jpg";
        builder.address = "Demo Street 1";
        builder.postalCode = "28001";
        builder.country = "Spain";
        builder.nationalId = "X1234567A";
        builder.gender = UserGender.MALE;

        return builder;
    }

    public UserProfileTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserProfileTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserProfileTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserProfileTestBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserProfileTestBuilder withBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public UserProfileTestBuilder withPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    public UserProfileTestBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public UserProfileTestBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public UserProfileTestBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public UserProfileTestBuilder withNationalId(String nationalId) {
        this.nationalId = nationalId;
        return this;
    }

    public UserProfileTestBuilder withGender(UserGender gender) {
        this.gender = gender;
        return this;
    }

    public UserProfile build() {
        return UserProfile.create()
                          .setId(id)
                          .setFirstName(firstName)
                          .setLastName(lastName)
                          .setPhoneNumber(phoneNumber)
                          .setBirthdate(birthdate)
                          .setPhotoPath(photo)
                          .setAddress(address)
                          .setPostalCode(postalCode)
                          .setCountry(country)
                          .setNationalId(nationalId)
                          .setGender(gender);
    }
}