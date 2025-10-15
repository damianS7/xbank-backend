package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.enums.UserAccountStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserAccountRole role;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private UserAccountStatus accountStatus;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer customer;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public UserAccount() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.role = UserAccountRole.CUSTOMER;
        this.accountStatus = UserAccountStatus.PENDING_VERIFICATION;
    }

    public UserAccount(Customer customer) {
        this();
        this.customer = customer;
    }

    public static UserAccount create() {
        return new UserAccount();
    }

    public Long getId() {
        return id;
    }

    public UserAccount setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public UserAccount setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return this.passwordHash;
    }

    public UserAccount setPassword(String password) {
        this.passwordHash = password;
        return this;
    }

    public UserAccountRole getRole() {
        return this.role;
    }

    public UserAccount setRole(UserAccountRole role) {
        this.role = role;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserAccount setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserAccount setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserAccountStatus getAccountStatus() {
        return this.accountStatus;
    }

    public UserAccount setAccountStatus(UserAccountStatus status) {
        this.accountStatus = status;
        return this;
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

    public Customer getCustomer() {
        return customer;
    }

    public UserAccount setCustomer(Customer customer) {
        this.customer = customer;
        if (this.customer.getAccount() == null) {
            this.customer.setAccount(this);
        }
        return this;
    }
}
