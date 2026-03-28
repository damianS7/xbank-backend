package com.damian.xBank.shared.utils;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestFactory {
    public final static String RAW_PASSWORD = "123456";

    public static User aCustomer() {
        return UserTestBuilder.builder()
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .withProfile(UserProfileTestFactory.testProfile())
            .build();
    }

    public static User aCustomerWithId(Long id) {
        return UserTestBuilder.builder()
            .withId(id)
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .withProfile(UserProfileTestFactory.testProfile())
            .build();
    }

    public static User anAdmin() {
        return UserTestBuilder.builder()
            .withId(1L)
            .withEmail("admin@demo.com")
            .withPassword("123456")
            .withRole(UserRole.ADMIN)
            .withStatus(UserStatus.VERIFIED)
            .build();
    }

}