package com.damian.xBank.modules.banking.account.service;

import com.damian.xBank.modules.banking.account.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.account.validator.BankingAccountGuard;
import com.damian.xBank.modules.banking.card.dto.request.BankingCardRequest;
import com.damian.xBank.modules.banking.card.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.card.service.BankingCardService;
import com.damian.xBank.shared.domain.BankingAccount;
import com.damian.xBank.shared.domain.BankingCard;
import com.damian.xBank.shared.domain.Customer;
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

    /**
     * Request a new banking card for the given banking account.
     *
     * @param bankingAccountId
     * @param request
     * @return the created BankingCard
     */
    public BankingCard requestCard(Long bankingAccountId, BankingCardRequest request) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // we get the BankingAccount to associate the card created.
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(
                                Exceptions.BANKING.ACCOUNT.NOT_FOUND, bankingAccountId
                        )
                );

        // if the logged customer is not admin
        if (!AuthHelper.isAdmin(currentCustomer)) {
            // check if the account belongs to this customer.
            BankingAccountGuard.forAccount(bankingAccount)
                               .ownership(currentCustomer);
        }

        // if customer has reached the maximum amount of cards per account.
        if (countActiveCards(bankingAccount) >= MAX_CARDS_PER_ACCOUNT) {
            throw new BankingAccountCardsLimitException(
                    Exceptions.BANKING.ACCOUNT.CARD_LIMIT, bankingAccountId
            );
        }

        // create the card and associate to the account and return it.
        return bankingCardService.createBankingCard(bankingAccount, request.type());
    }

    /**
     * Counts how many active (ENABLED) cards are associated with the given banking account.
     *
     * @param bankingAccount
     * @return the number of active cards
     */
    private int countActiveCards(BankingAccount bankingAccount) {
        return (int) bankingAccount
                .getBankingCards()
                .stream()
                .filter(bankingCard -> bankingCard.getCardStatus().equals(BankingCardStatus.ENABLED))
                .count();
    }
}
