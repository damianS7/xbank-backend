package com.damian.xBank.modules.user.user.domain.model;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.setting.domain.model.UserSettings;
import com.damian.xBank.modules.user.merchant.domain.Merchant;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.exception.UserNotMerchantException;
import com.damian.xBank.modules.user.user.domain.exception.UserVerificationNotPendingException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA constructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    private Merchant merchant;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Setting settings;

    public static User reconstitute(
        Long id,
        String email,
        String passwordHash,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt,
        Merchant merchant,
        UserProfile profile,
        Setting settings
    ) {
        return new User(id, role, email, passwordHash, status, createdAt, updatedAt, merchant, profile, settings);
    }

    public static User create(
        String email,
        String passwordHash,
        UserRole role
    ) {
        User user = new User(
            null,
            role,
            email,
            passwordHash,
            UserStatus.PENDING_VERIFICATION,
            Instant.now(),
            Instant.now(),
            null,
            null,
            null
        );

        user.createProfile();
        user.createSettings();

        return user;
    }

    public void assignProfile(UserProfile profile) {
        if (profile == null) {
            return;
        }
        
        this.profile = profile;
        this.profile.assignOwner(this);
    }

    private void createProfile() {
        assignProfile(UserProfile.emptyProfile());
    }

    public void assignSettings(Setting settings) {
        this.settings = settings;
        this.settings.assignOwner(this);
    }

    private void createSettings() {
        assignSettings(Setting.create());
    }

    public void updateSettings(UserSettings settings) {
        this.settings.updateSettings(settings);
    }

    public Merchant registerMerchant(
        String merchantName,
        String callbackUrl
    ) {
        this.merchant = Merchant.create(merchantName, callbackUrl);
        this.merchant.assignUser(this);
        return this.merchant;
    }

    public boolean hasRole(UserRole role) {
        return this.getRole() == role;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            throw new UserNotMerchantException(this.id);
        }
        return merchant;
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

    public void assertAwaitingVerification() {
        if (this.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw new UserVerificationNotPendingException(getId());
        }
    }

    public void verifyAccount() {
        assertAwaitingVerification();
        setStatus(UserStatus.VERIFIED);
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
