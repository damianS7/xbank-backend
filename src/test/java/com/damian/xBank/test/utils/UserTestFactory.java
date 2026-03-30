package com.damian.xBank.test.utils;

import com.damian.xBank.modules.user.merchant.domain.Merchant;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestFactory {
    public final static String RAW_PASSWORD = "123456";

    public static UserTestBuilder aUser() {
        return UserTestBuilder.builder()
            .withEmail("customer@demo.com")
            .withPassword(RAW_PASSWORD)
            .withRole(UserRole.CUSTOMER)
            .withStatus(UserStatus.VERIFIED)
            .withProfile(UserProfileTestFactory.testProfile());
    }

    public static UserTestBuilder aMerchant() {
        Merchant merchant = Merchant.create("Amazon.es", "https://amazon.es");
        return aUser()
            .withRole(UserRole.MERCHANT)
            .withMerchant(merchant);
    }

    public static UserTestBuilder aCustomer() {
        return aUser().withRole(UserRole.CUSTOMER);
    }

    public static UserTestBuilder anAdmin() {
        return aUser()
            .withEmail("admin@demo.com")
            .withRole(UserRole.ADMIN);
    }
}