package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.banking.account.BankingAccount;
import com.damian.xBank.modules.user.customer.CustomerGender;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
        this.bankingAccounts = new HashSet<>();
    }

    public Customer(UserAccount account) {
        this();
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Set<BankingAccount> getBankingAccounts() {
        return bankingAccounts;
    }

    public void addBankingAccount(BankingAccount bankingAccount) {
        if (bankingAccount.getOwner() != this) {
            bankingAccount.setOwner(this);
        }

        this.bankingAccounts.add(bankingAccount);
    }

    public void setBankingAccounts(Set<BankingAccount> bankingAccounts) {
        this.bankingAccounts = bankingAccounts;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhotoPath() {
        return photo;
    }

    public void setPhotoPath(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }


    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", updatedAt=" + updatedAt +
               '}';

    }

    public CustomerGender getGender() {
        return gender;
    }

    public void setGender(CustomerGender gender) {
        this.gender = gender;
    }

    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }
}
