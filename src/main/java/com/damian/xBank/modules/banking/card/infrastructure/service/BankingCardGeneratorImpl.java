package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

@Component
public class BankingCardGeneratorImpl implements BankingCardGenerator {
    private final Faker faker;

    public BankingCardGeneratorImpl(
            Faker faker
    ) {
        this.faker = faker;
    }

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
        return faker.number().digits(16);
    }

    @Override
    public String generateCvv() {
        return faker.number().digits(3);
    }

    @Override
    public String generatePin() {
        return faker.number().digits(4);
    }
}