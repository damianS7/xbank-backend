package com.damian.xBank.modules.user.user.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
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
    private UserRole role;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

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
        this.role = UserRole.CUSTOMER;
        this.status = UserStatus.PENDING_VERIFICATION;
        this.profile = new UserProfile(this);
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

    public void changePassword(String newHashedPassword) {
        this.passwordHash = newHashedPassword;
        this.updatedAt = Instant.now();
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

    public User setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public UserRole getRole() {
        return this.role;
    }

    public boolean hasRole(UserRole role) {
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

    public UserStatus getStatus() {
        return this.status;
    }

    public User setStatus(UserStatus status) {
        this.status = status;
        return this;
    }

    public boolean isAdmin() {
        return this.getRole() == UserRole.ADMIN;
    }

    public boolean isCustomer() {
        return this.getRole() == UserRole.CUSTOMER;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
               "id=" + id +
               ", role=" + role +
               ", email='" + email + '\'' +
               ", passwordHash='" + passwordHash + '\'' +
               ", accountStatus=" + status +
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

    public void assertAwaitingVerification() {
        if (this.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new UserVerificationNotPendingException(getId());
        }
    }

    public void verifyAccount() {
        assertAwaitingVerification();
        setStatus(UserStatus.VERIFIED);
        //        this.status = UserStatus.VERIFIED;
        this.updatedAt = Instant.now();
    }
}
