package com.damian.xBank.modules.banking.account.infrastructure.service;

import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class BankingAccountNumberGeneratorImpl implements BankingAccountNumberGenerator {
    private final Faker faker;

    public BankingAccountNumberGeneratorImpl(
            Faker faker
    ) {
        this.faker = faker;
    }

    /**
     * Generate a random account number.
     *
     * @return String account number
     */
    @Override
    public String generate() {
        //ES00 0000 0000 0000 0000 0000
        String country = faker.country().countryCode2().toUpperCase();
        return country + faker.number().digits(22);
    }
}