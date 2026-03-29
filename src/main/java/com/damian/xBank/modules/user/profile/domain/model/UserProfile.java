package com.damian.xBank.modules.user.profile.domain.model;

import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA constructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phoneNumber;

    @Column
    private LocalDate birthdate;

    @Column(name = "photo")
    private String photo;

    @Column
    private String address;

    @Column
    private String postalCode;

    @Column
    private String country;

    @Column
    private String nationalId;

    @Column
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column
    private Instant updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public static UserProfile reconstitute(
        Long profileId,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthdate,
        String photo,
        String address,
        String postalCode,
        String country,
        String nationalId,
        UserGender gender,
        Instant updatedAt
    ) {
        return new UserProfile(
            profileId,
            firstName,
            lastName,
            phoneNumber,
            birthdate,
            photo,
            address,
            postalCode,
            country,
            nationalId,
            gender,
            updatedAt,
            null
        );
    }

    public static UserProfile create(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthdate,
        String photo,
        String address,
        String postalCode,
        String country,
        String nationalId,
        UserGender gender
    ) {
        return new UserProfile(
            null,
            firstName,
            lastName,
            phoneNumber,
            birthdate,
            photo,
            address,
            postalCode,
            country,
            nationalId,
            gender,
            Instant.now(),
            null
        );
    }

    public static UserProfile emptyProfile() {
        return new UserProfile(
            null,
            "", "", "", LocalDate.now(), "avatar.jpg",
            "", "", "", "", UserGender.MALE, Instant.now(), null
        );
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public UserProfile setFirstName(String firstName) {
        this.firstName = firstName;
        markAsUpdated();
        return this;
    }

    public UserProfile setLastName(String lastName) {
        this.lastName = lastName;
        markAsUpdated();
        return this;
    }

    public UserProfile setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        markAsUpdated();
        return this;
    }

    public UserProfile setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        markAsUpdated();
        return this;
    }

    public String getPhotoPath() {
        return photo;
    }

    public UserProfile setPhotoPath(String photo) {
        this.photo = photo;
        markAsUpdated();
        return this;
    }

    public UserProfile setAddress(String address) {
        this.address = address;
        markAsUpdated();
        return this;
    }

    public UserProfile setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        markAsUpdated();
        return this;
    }

    public UserProfile setCountry(String country) {
        this.country = country;
        markAsUpdated();
        return this;
    }

    public UserProfile setNationalId(String nationalId) {
        this.nationalId = nationalId;
        markAsUpdated();
        return this;
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public UserProfile setGender(UserGender gender) {
        this.gender = gender;
        markAsUpdated();
        return this;
    }

    public void assignOwner(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", birthdate=" + birthdate +
               ", photo='" + photo + '\'' +
               ", address='" + address + '\'' +
               ", zipCode='" + postalCode + '\'' +
               ", country='" + country + '\'' +
               ", nationalId='" + nationalId + '\'' +
               ", gender=" + gender +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
