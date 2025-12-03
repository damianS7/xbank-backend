package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    private final UserAccount account;

    public User(UserAccount account) {
        this.account = account;
    }

    public static User create() {
        return new User(
                UserAccount.create()
        );
    }

    public Long getId() {
        return account.getId();
    }

    public User setId(Long id) {
        this.account.setId(id);
        return this;
    }

    public User setEmail(String email) {
        this.account.setEmail(email);
        return this;
    }

    public User setPassword(String password) {
        this.account.setPassword(password);
        return this;
    }

    public Customer getCustomer() {
        return account.getCustomer();
    }

    public UserAccount getAccount() {
        return account;
    }

    public String getEmail() {
        return account.getEmail();
    }

    public User setUpdatedAt(Instant updatedAt) {
        this.account.setUpdatedAt(updatedAt);
        return this;
    }

    public User setCreatedAt(Instant createdAt) {
        this.account.setCreatedAt(createdAt);
        return this;
    }

    public User setAccountStatus(UserAccountStatus status) {
        this.account.setAccountStatus(status);
        return this;
    }

    public UserAccountRole getRole() {
        return account.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + account.getRole().name());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !account.getAccountStatus().equals(UserAccountStatus.SUSPENDED);
    }

    @Override
    public boolean isEnabled() {
        return account.getAccountStatus().equals(UserAccountStatus.VERIFIED);
    }
}
