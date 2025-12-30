package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Service
public class BankingCardService {

    private final AuthenticationContext authenticationContext;
    private final BankingCardRepository bankingCardRepository;
    private final Faker faker;

    public BankingCardService(
            AuthenticationContext authenticationContext,
            BankingCardRepository bankingCardRepository,
            Faker faker
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingCardRepository = bankingCardRepository;
        this.faker = faker;
    }

    // return the cards of the logged customer
    public Set<BankingCard> getCustomerBankingCards() {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        return this.getCustomerBankingCards(currentCustomer.getId());
    }

    // return the cards of a customer
    public Set<BankingCard> getCustomerBankingCards(Long customerId) {
        return bankingCardRepository.findCardsByCustomerId(customerId);
    }

    // create a new card and associate to the account
    public BankingCard createBankingCard(
            BankingAccount bankingAccount,
            BankingCardType cardType
    ) {
        // create the card and associate to the account
        BankingCard bankingCard = new BankingCard();
        bankingCard.setCardCvv(this.generateCardCVV());
        bankingCard.setCardPin(this.generateCardPIN());
        bankingCard.setCardNumber(this.generateCardNumber());
        bankingCard.setExpiredDate(LocalDate.now().plusMonths(24));
        bankingCard.setCardType(cardType);
        bankingCard.setCreatedAt(Instant.now());
        bankingCard.setUpdatedAt(Instant.now());
        bankingCard.setBankingAccount(bankingAccount);

        // save the card
        return bankingCardRepository.save(bankingCard);
    }

    public String generateCardNumber() {
        return faker.number().digits(16);
    }

    public String generateCardCVV() {
        return faker.number().digits(3);
    }

    public String generateCardPIN() {
        return faker.number().digits(4);
    }
}
