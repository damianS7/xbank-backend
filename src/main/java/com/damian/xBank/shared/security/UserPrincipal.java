package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.modules.user.account.account.domain.model.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.model.UserAccountStatus;
import com.damian.xBank.modules.user.profile.domain.entity.UserProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public static UserPrincipal create() {
        return new UserPrincipal(
                User.create()
        );
    }

    public UserProfile getProfile() {
        return user.getUserProfile();
    }

    public Long getId() {
        return user.getId();
    }

    public UserPrincipal setId(Long id) {
        this.user.setId(id);
        return this;
    }

    public UserPrincipal setEmail(String email) {
        this.user.setEmail(email);
        return this;
    }

    public UserPrincipal setPassword(String password) {
        this.user.setPassword(password);
        return this;
    }

    public UserProfile getCustomer() {
        return user.getUserProfile();
    }

    public User getUser() {
        return user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public UserPrincipal setUpdatedAt(Instant updatedAt) {
        this.user.setUpdatedAt(updatedAt);
        return this;
    }

    public UserPrincipal setCreatedAt(Instant createdAt) {
        this.user.setCreatedAt(createdAt);
        return this;
    }

    public UserPrincipal setAccountStatus(UserAccountStatus status) {
        this.user.setAccountStatus(status);
        return this;
    }

    public UserAccountRole getRole() {
        return user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return user.getAccountStatus() != UserAccountStatus.SUSPENDED;
    }

    @Override
    public boolean isEnabled() {
        return user.getAccountStatus() == UserAccountStatus.VERIFIED;
    }
}
