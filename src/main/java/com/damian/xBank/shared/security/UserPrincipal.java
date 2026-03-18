package com.damian.xBank.shared.security;

import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public UserProfile getProfile() {
        return user.getProfile();
    }

    public Long getId() {
        return user.getId();
    }

    public UserPrincipal setEmail(String email) {
        this.user.changeEmail(email);
        return this;
    }

    public UserPrincipal setPassword(String password) {
        this.user.changePassword(password);
        return this;
    }

    public UserProfile getCustomer() {
        return user.getProfile();
    }

    public User getUser() {
        return user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public UserRole getRole() {
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
        return user.getStatus() != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.VERIFIED;
    }
}
