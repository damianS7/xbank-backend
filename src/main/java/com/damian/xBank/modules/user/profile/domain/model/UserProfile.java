package com.damian.xBank.modules.user.profile.domain.model;

import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected UserProfile() {
        // JPA constructor
    }

    UserProfile(
        Long profileId,
        User user,
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
        this.id = profileId;
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.postalCode = postalCode;
        this.country = country;
        this.nationalId = nationalId;
        this.gender = gender;
        this.updatedAt = Instant.now();
    }

    public static UserProfile create(User owner) {
        UserProfile profile = UserProfileFactory.defaultProfile();
        profile.user = owner;
        return profile;
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
        UserProfile profile = new UserProfile();
        profile.firstName = firstName;
        profile.lastName = lastName;
        profile.phoneNumber = phoneNumber;
        profile.birthdate = birthdate;
        profile.photo = photo;
        profile.address = address;
        profile.postalCode = postalCode;
        profile.country = country;
        profile.nationalId = nationalId;
        profile.gender = gender;
        profile.updatedAt = Instant.now();
        return profile;
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

    public void setUser(User user) {
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
