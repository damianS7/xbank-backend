package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.service.BankingCardDomainService;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingAccountCardCreate {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final BankingCardDomainService bankingCardDomainService;
    private final BankingCardRepository bankingCardRepository;

    public BankingAccountCardCreate(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext,
            BankingCardDomainService bankingCardDomainService,
            BankingCardRepository bankingCardRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.bankingCardDomainService = bankingCardDomainService;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Request a new banking card for the given banking account.
     *
     * @param bankingAccountId
     * @param request
     * @return the created BankingCard
     */
    @Transactional
    public BankingCard execute(Long bankingAccountId, BankingAccountCardRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // we get the BankingAccount to associate the card created.
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(bankingAccountId)
                );

        // if the logged customer is not admin
        if (!currentUser.isAdmin()) {
            // check if the account belongs to this customer.
            bankingAccount.assertOwnedBy(currentUser.getId());
        }

        // if customer has reached the maximum amount of cards per account.
        bankingAccount.assertCanAddCard();

        // create the card and associate to the account and return it.
        BankingCard card = bankingCardDomainService.createBankingCard(bankingAccount, request.type());

        // generate another card if number exists
        while (bankingCardRepository.existsByCardNumber(card.getCardNumber())) {
            card = bankingCardDomainService.createBankingCard(bankingAccount, request.type());
        }

        bankingAccount.addBankingCard(card);

        return card;
    }
}