package com.damian.xBank.modules.user.profile.domain.model;

import com.damian.xBank.modules.user.user.domain.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phone;

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

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column
    private Instant updatedAt;

    public UserProfile() {
        this.user = new User(this);
        this.updatedAt = Instant.now();
    }

    public static UserProfile create() {
        return new UserProfile();
    }

    public UserProfile(User user) {
        this();
        this.setUser(user);
    }

    public Long getId() {
        return id;
    }

    public UserProfile setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserProfile setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserProfile setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserProfile setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserProfile setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public UserProfile setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public String getPhotoPath() {
        return photo;
    }

    public UserProfile setPhotoPath(String photo) {
        this.photo = photo;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserProfile setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public UserProfile setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public UserProfile setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getNationalId() {
        return nationalId;
    }

    public UserProfile setNationalId(String nationalId) {
        this.nationalId = nationalId;
        return this;
    }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", account=" + user.toString() +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phoneNumber='" + phone + '\'' +
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

    public UserGender getGender() {
        return gender;
    }

    public UserProfile setGender(UserGender gender) {
        this.gender = gender;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserProfile setUser(User user) {
        this.user = user;
        if (this.user.getProfile() == null) {
            this.user.setProfile(this);
        }
        return this;
    }
}
