package com.damian.xBank.shared.domain;

import com.damian.xBank.modules.user.account.account.UserAccountStatus;
import com.damian.xBank.modules.user.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final UserAccount account;

    public UserPrincipal(UserAccount account) {
        this.account = account;
    }

    public UserAccount getAccount() {
        return account;
    }

    public String getEmail() {
        return account.getEmail();
    }

    public UserRole getRole() {
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
