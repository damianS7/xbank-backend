package com.damian.xBank.test.utils;

import com.damian.xBank.modules.user.merchant.domain.Merchant;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestFactory {
    public final static String RAW_PASSWORD = "123456";

    public static UserTestBuilder aMerchant() {
        return UserTestBuilder.builder()
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withRole(UserRole.MERCHANT)
            .withStatus(UserStatus.VERIFIED)
            .withProfile(UserProfileTestFactory.testProfile())
            .withMerchant(Merchant.create("Amazon.es", "https://amazon.es"));
    }

    public static UserTestBuilder aCustomer() {
        return UserTestBuilder.builder()
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .withProfile(UserProfileTestFactory.testProfile());
    }

    public static UserTestBuilder anAdmin() {
        return UserTestBuilder.builder()
            .withEmail("admin@demo.com")
            .withPassword("123456")
            .withRole(UserRole.ADMIN)
            .withStatus(UserStatus.VERIFIED);
    }
}