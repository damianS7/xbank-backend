package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;

@Component
public class BankingCardGeneratorImpl implements BankingCardGenerator {
    @Value("${bank.card.bin}")
    private String BIN;

    // create a new card and associate to the account
    public BankingCard generate(
            BankingAccount bankingAccount,
            BankingCardType cardType
    ) {
        // create the card and associate to the account
        BankingCard bankingCard = new BankingCard();
        bankingCard.setCardCvv(this.generateCvv());
        bankingCard.setCardPin(this.generatePin());
        bankingCard.setCardNumber(this.generateCardNumber());
        bankingCard.setExpiredDate(LocalDate.now().plusMonths(24));
        bankingCard.setCardType(cardType);
        bankingCard.setCreatedAt(Instant.now());
        bankingCard.setUpdatedAt(Instant.now());
        bankingCard.setBankingAccount(bankingAccount);

        return bankingCard;
    }

    @Override
    public String generateCardNumber() {
        SecureRandom random = new SecureRandom();

        if (BIN == null) {
            BIN = String.valueOf(random.nextInt(999999));
        }

        int digit = random.nextInt(999999999);
        return BIN + String.format("%010d", digit);
    }

    @Override
    public String generateCvv() {
        SecureRandom random = new SecureRandom();
        int digit = random.nextInt(999);
        return String.format("%03d", digit);
    }

    @Override
    public String generatePin() {
        SecureRandom random = new SecureRandom();
        int digit = random.nextInt(9999);
        return String.format("%04d", digit);
    }
}