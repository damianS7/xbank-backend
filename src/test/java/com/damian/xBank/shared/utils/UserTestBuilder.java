package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;
import com.damian.xBank.modules.user.user.domain.model.UserAccountStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserTestBuilder {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Long id = null;
    private String email = "user@demo.com";
    private String password = "$2a$10$7EqJtq98hPqEX7fNZaFWoOa6sK9Pz7RrH9Z4VQe8C7l8bqZkYwF6e";
    private UserAccountStatus status = UserAccountStatus.VERIFIED;
    private UserAccountRole role = UserAccountRole.CUSTOMER;
    private UserProfile profile;

    private UserTestBuilder() {
    }

    // Builder
    public static UserTestBuilder aCustomer() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withStatus(UserAccountStatus status) {
        this.status = status;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withRole(UserAccountRole role) {
        this.role = role;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = passwordEncoder.encode(password);
        return this;
    }

    public UserTestBuilder withProfile(UserProfile profile) {
        this.profile = profile;
        return this;
    }

    public User build() {
        return User.create()
                   .setId(id)
                   .setPassword(password)
                   .setAccountStatus(status)
                   .setEmail(email)
                   .setProfile(profile)
                   .setRole(role);
    }
}