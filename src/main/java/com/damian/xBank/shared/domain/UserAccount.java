package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.user.account.account.UserAccountStatus;
import com.damian.xBank.modules.user.user.enums.UserRole;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private UserAccountStatus accountStatus;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer customer;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public UserAccount() {
        this.customer = new Customer(this);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.role = UserRole.USER;
        this.accountStatus = UserAccountStatus.PENDING_VERIFICATION;
    }

    public UserAccount(Long userId, String email, String password) {
        this();
        this.id = userId;
        this.email = email;
        this.passwordHash = password;
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
        this.passwordHash = passwordHash;
        return this;
    }

    public UserRole getRole() {
        return this.role;
    }

    public UserAccount setRole(UserRole role) {
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
        return "User {" +
               " id=" + id +
               ", email=" + email +
               ", password=" + passwordHash +
               ", account_status=" + getAccountStatus() +
               ", role=" + role +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
