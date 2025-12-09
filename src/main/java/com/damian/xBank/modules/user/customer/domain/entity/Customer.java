package com.damian.xBank.modules.user.customer.domain.entity;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingAccount> bankingAccounts;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount account;

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
    private CustomerGender gender;

    @Column
    private Instant updatedAt;

    public Customer() {
        this.account = new UserAccount(this);
        this.bankingAccounts = new HashSet<>();
        this.updatedAt = Instant.now();
    }

    public static Customer create() {
        return new Customer();
    }

    public Customer(UserAccount account) {
        this();
        this.setAccount(account);
    }

    public static Customer create(UserAccount account) {
        return new Customer(account);
    }

    public Customer setEmail(String email) {
        this.account.setEmail(email);
        return this;
    }

    public Customer setPassword(String password) {
        this.account.setPassword(password);
        return this;
    }

    public Customer setRole(UserAccountRole role) {
        this.account.setRole(role);
        return this;
    }

    public UserAccountRole getRole() {
        return this.account.getRole();
    }

    public Long getId() {
        return id;
    }

    public Customer setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Set<BankingAccount> getBankingAccounts() {
        return bankingAccounts;
    }

    public Customer addBankingAccount(BankingAccount bankingAccount) {
        if (bankingAccount.getOwner() != this) {
            bankingAccount.setOwner(this);
        }

        this.bankingAccounts.add(bankingAccount);
        return this;
    }

    public Customer setBankingAccounts(Set<BankingAccount> bankingAccounts) {
        this.bankingAccounts = bankingAccounts;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Customer setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Customer setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Customer setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Customer setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public Customer setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public String getPhotoPath() {
        return photo;
    }

    public Customer setPhotoPath(String photo) {
        this.photo = photo;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Customer setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Customer setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Customer setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getNationalId() {
        return nationalId;
    }

    public Customer setNationalId(String nationalId) {
        this.nationalId = nationalId;
        return this;
    }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", bankingAccounts=" + bankingAccounts +
               ", account=" + account.toString() +
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

    public CustomerGender getGender() {
        return gender;
    }

    public Customer setGender(CustomerGender gender) {
        this.gender = gender;
        return this;
    }

    public UserAccount getAccount() {
        return account;
    }

    public Customer setAccount(UserAccount account) {
        this.account = account;
        if (this.account.getCustomer() == null) {
            this.account.setCustomer(this);
        }
        return this;
    }

    public String getEmail() {
        return account.getEmail();
    }
}
