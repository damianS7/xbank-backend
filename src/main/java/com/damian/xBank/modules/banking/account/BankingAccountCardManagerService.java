package com.damian.xBank.modules.banking.account;

import com.damian.xBank.modules.banking.account.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.card.BankingCard;
import com.damian.xBank.modules.banking.card.BankingCardService;
import com.damian.xBank.modules.banking.card.BankingCardStatus;
import com.damian.xBank.modules.banking.card.exception.BankingCardMaximumCardsPerAccountLimitReached;
import com.damian.xBank.modules.banking.card.http.BankingCardRequest;
import com.damian.xBank.modules.customer.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountCardManagerService {
    private final int MAX_CARDS_PER_ACCOUNT = 5;
    private final BankingCardService bankingCardService;
    private final BankingAccountRepository bankingAccountRepository;

    public BankingAccountCardManagerService(
            BankingAccountRepository bankingAccountRepository,
            BankingCardService bankingCardService
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingCardService = bankingCardService;
    }

    public BankingCard requestBankingCard(Long bankingAccountId, BankingCardRequest request) {
        // Customer logged
        final Customer customerLogged = AuthHelper.getLoggedCustomer();

        // we get the BankingAccount to associate the card created.
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                Exceptions.ACCOUNT.NOT_FOUND
                        )
                );

        // if the logged customer is not admin
        if (!AuthHelper.isAdmin(customerLogged)) {
            // check if the account belongs to this customer.
            BankingAccountAuthorizationHelper
                    .authorize(customerLogged, bankingAccount)
                    .checkOwner();
        }

        // if customer has reached the maximum amount of cards per account.
        if (countActiveCards(bankingAccount) >= MAX_CARDS_PER_ACCOUNT) {
            throw new BankingCardMaximumCardsPerAccountLimitReached(
                    Exceptions.ACCOUNT.CARD_LIMIT
            );
        }

        // create the card and associate to the account and return it.
        return bankingCardService.createBankingCard(bankingAccount, request.cardType());
    }

    // It counts how many active (ENABLED) cards has this account
    private int countActiveCards(BankingAccount bankingAccount) {
        return (int) bankingAccount
                .getBankingCards()
                .stream()
                .filter(bankingCard -> bankingCard.getCardStatus().equals(BankingCardStatus.ENABLED))
                .count();
    }
}
