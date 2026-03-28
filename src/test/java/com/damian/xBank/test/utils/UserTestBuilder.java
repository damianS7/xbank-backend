package com.damian.xBank.test.utils;

import com.damian.xBank.modules.setting.domain.model.Setting;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestBuilder {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Long id = null;
    private String email = "user@demo.com";
    private String password = "$2a$10$7EqJtq98hPqEX7fNZaFWoOa6sK9Pz7RrH9Z4VQe8C7l8bqZkYwF6e";
    private UserStatus status = UserStatus.VERIFIED;
    private UserRole role = UserRole.CUSTOMER;
    private UserProfile profile = UserProfileTestFactory.testProfile();
    private Setting settings = Setting.create();

    public static UserTestBuilder builder() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withStatus(UserStatus status) {
        this.status = status;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withRole(UserRole role) {
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
        User user = User.reconstitute(
            id,
            email,
            password,
            role,
            status,
            Instant.now(),
            Instant.now(),
            null,
            profile,
            settings
        );

        profile.assignOwner(user);
        settings.assignOwner(user);

        return user;
    }
}