package com.damian.xBank.modules.user.user.domain.model;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.token.domain.model.UserToken;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_accounts")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Setting settings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserToken token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BankingAccount> bankingAccounts;

    protected User() {
        // JPA constructor
    }

    User(
        Long id,
        String email,
        String passwordHash,
        UserRole role,
        UserStatus status,
        UserProfile profile
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : UserRole.CUSTOMER;
        this.status = status != null ? status : UserStatus.PENDING_VERIFICATION;
        this.settings = Setting.create(this, null);
        this.bankingAccounts = new HashSet<>();
        this.profile = profile != null ? profile : UserProfile.create(this);
        this.profile.setUser(this);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static User create(
        String email,
        String passwordHash,
        UserRole role
    ) {
        return new User(null, email, passwordHash, role, null, null);
    }

    public static User create(
        String email,
        String passwordHash,
        UserRole role,
        UserProfile profile
    ) {
        return new User(null, email, passwordHash, role, null, profile);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.passwordHash;
    }

    public Set<BankingAccount> getBankingAccounts() {
        return bankingAccounts;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public Setting getSettings() {
        return settings;
    }

    public UserToken getToken() {
        return token;
    }

    public boolean isAdmin() {
        return this.getRole() == UserRole.ADMIN;
    }

    public boolean isCustomer() {
        return this.getRole() == UserRole.CUSTOMER;
    }

    private void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    public void changeEmail(String email) {
        this.email = email;
        markAsUpdated();
    }

    public void changePassword(String newHashedPassword) {
        this.passwordHash = newHashedPassword;
        markAsUpdated();
    }

    private void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setToken(UserToken activationToken) {
        this.token = activationToken;
    }

    public void assignProfile(UserProfile newProfile) {
        if (newProfile != null) {
            this.profile = newProfile;
        }

        //        this.profile.setUser(this);
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

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", role=" + role +
               ", email='" + email + '\'' +
               ", passwordHash='" + passwordHash + '\'' +
               ", accountStatus=" + status +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }


}
