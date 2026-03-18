package com.damian.xBank.modules.user.profile.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

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

    protected UserProfile() {
        // JPA constructor
    }

    UserProfile(
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
        UserGender gender
    ) {
        this();
        this.id = profileId;
        this.updatedAt = Instant.now();
    }

    public static UserProfile create() {
        return new UserProfile();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserProfile setFirstName(String firstName) {
        this.firstName = firstName;
        markAsUpdated();
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserProfile setLastName(String lastName) {
        this.lastName = lastName;
        markAsUpdated();
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserProfile setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        markAsUpdated();
        return this;
    }

    public LocalDate getBirthdate() {
        return birthdate;
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

    public String getAddress() {
        return address;
    }

    public UserProfile setAddress(String address) {
        this.address = address;
        markAsUpdated();
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public UserProfile setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        markAsUpdated();
        return this;
    }

    public String getCountry() {
        return country;
    }

    public UserProfile setCountry(String country) {
        this.country = country;
        markAsUpdated();
        return this;
    }

    public String getNationalId() {
        return nationalId;
    }

    public UserProfile setNationalId(String nationalId) {
        this.nationalId = nationalId;
        markAsUpdated();
        return this;
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public UserGender getGender() {
        return gender;
    }

    public UserProfile setGender(UserGender gender) {
        this.gender = gender;
        markAsUpdated();
        return this;
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
