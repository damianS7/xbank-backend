package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.modules.banking.card.domain.service.BankingCardGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class BankingCardGeneratorImpl implements BankingCardGenerator {

    private final String BIN;
    private final SecureRandom random = new SecureRandom();

    public BankingCardGeneratorImpl(
        @Value("${bank.card.bin}") String bin
    ) {
        BIN = bin;
    }

    @Override
    public String generateCardNumber() {
        String BIN = this.BIN;

        if (BIN == null) {
            BIN = String.valueOf(random.nextInt(999999));
        }

        int digit = random.nextInt(999999999);
        return BIN + String.format("%010d", digit);
    }

    @Override
    public String generateCvv() {
        int digit = random.nextInt(999);
        return String.format("%03d", digit);
    }

    @Override
    public String generatePin() {
        int digit = random.nextInt(9999);
        return String.format("%04d", digit);
    }
}