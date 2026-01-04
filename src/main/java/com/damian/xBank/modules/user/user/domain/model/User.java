package com.damian.xBank.modules.user.user.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_accounts")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingAccount> bankingAccounts;

    @Enumerated(EnumType.STRING)
    private UserAccountRole role;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private UserAccountStatus accountStatus;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public User() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.bankingAccounts = new HashSet<>();
        this.role = UserAccountRole.CUSTOMER;
        this.accountStatus = UserAccountStatus.PENDING_VERIFICATION;
    }

    public User(UserProfile profile) {
        this();
        this.profile = profile;
    }

    public static User create() {
        return new User();
    }

    public static User create(UserProfile userProfile) {
        return new User(userProfile);
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return this.passwordHash;
    }

    public User setPassword(String password) {
        this.passwordHash = password;
        return this;
    }

    public Set<BankingAccount> getBankingAccounts() {
        return bankingAccounts;
    }

    public User addBankingAccount(BankingAccount bankingAccount) {
        if (bankingAccount.getOwner() != this) {
            bankingAccount.setOwner(this);
        }

        this.bankingAccounts.add(bankingAccount);
        return this;
    }

    public User setBankingAccounts(Set<BankingAccount> bankingAccounts) {
        this.bankingAccounts = bankingAccounts;
        return this;
    }

    public User setRole(UserAccountRole role) {
        this.role = role;
        return this;
    }

    public UserAccountRole getRole() {
        return this.role;
    }

    public boolean hasRole(UserAccountRole role) {
        return this.getRole() == role;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public User setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserAccountStatus getAccountStatus() {
        return this.accountStatus;
    }

    public User setAccountStatus(UserAccountStatus status) {
        this.accountStatus = status;
        return this;
    }

    public boolean isAdmin() {
        return this.getRole() == UserAccountRole.ADMIN;
    }

    public boolean isCustomer() {
        return this.getRole() == UserAccountRole.CUSTOMER;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
               "id=" + id +
               ", role=" + role +
               ", email='" + email + '\'' +
               ", passwordHash='" + passwordHash + '\'' +
               ", accountStatus=" + accountStatus +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }

    public UserProfile getProfile() {
        return profile;
    }

    public User setProfile(UserProfile newProfile) {
        if (newProfile != null) {
            this.profile = newProfile;
        }

        this.profile.setUser(this);
        return this;
    }
}
