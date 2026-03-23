package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserTestFactory {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private UserTestFactory() {
    }

    public static User customer() {
        return UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode("123456"))
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .build();
    }

    public static User admin() {
        return UserTestBuilder.builder()
            .withId(1L)
            .withEmail("admin@demo.com")
            .withPassword(bCryptPasswordEncoder.encode("123456"))
            .withRole(UserRole.ADMIN)
            .withStatus(UserStatus.VERIFIED)
            .build();
    }

}