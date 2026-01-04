package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserTestFactory {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private UserTestFactory() {
    }

    public static User customer() {
        return User.create()
                   .setId(1L)
                   .setEmail("customer@demo.com")
                   .setPassword(bCryptPasswordEncoder.encode("123456"))
                   .setRole(UserRole.CUSTOMER)
                   .setStatus(UserStatus.VERIFIED);
    }

    public static User admin() {
        return User.create()
                   .setId(1L)
                   .setEmail("admin@demo.com")
                   .setPassword(bCryptPasswordEncoder.encode("123456"))
                   .setRole(UserRole.ADMIN)
                   .setStatus(UserStatus.VERIFIED);
    }

}