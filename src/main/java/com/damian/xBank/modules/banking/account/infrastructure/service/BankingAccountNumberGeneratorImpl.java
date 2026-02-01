package com.damian.xBank.modules.banking.account.infrastructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class BankingAccountNumberGeneratorImpl implements BankingAccountNumberGenerator {

    private final String BIN;

    public BankingAccountNumberGeneratorImpl(
            @Value("${bank.account.bin}") String BIN
    ) {
        this.BIN = BIN;
    }

    /**
     * Generate a random account number.
     *
     * @return String account number
     */
    @Override
    public String generate() {
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10)); // 0–9
        }

        return BIN + sb;
    }
}