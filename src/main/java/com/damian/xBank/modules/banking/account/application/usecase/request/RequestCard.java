package com.damian.xBank.modules.banking.account.application.usecase.request;

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
public class RequestCard {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final BankingCardDomainService bankingCardDomainService;
    private final BankingCardRepository bankingCardRepository;

    public RequestCard(
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
     * @param command
     * @return the created BankingCard
     */
    @Transactional
    public RequestCardResult execute(RequestCardCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // we get the BankingAccount to associate the card created.
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.bankingAccountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(command.bankingAccountId())
            );

        // if the logged customer is not admin
        if (!currentUser.isAdmin()) {
            // check if the account belongs to this customer.
            bankingAccount.assertOwnedBy(currentUser.getId());
        }

        // if customer has reached the maximum amount of cards per account.
        bankingAccount.assertCanAddCard();

        // create the card and associate to the account and return it.
        BankingCard card = bankingCardDomainService.createBankingCard(bankingAccount, command.type());

        // generate another card if number exists
        while (bankingCardRepository.existsByCardNumber(card.getCardNumber())) {
            card = bankingCardDomainService.createBankingCard(bankingAccount, command.type());
        }

        bankingAccount.addBankingCard(card);

        return RequestCardResult.from(card);
    }
}