package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.application.service.BankingCardService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

@Service
public class BankingAccountCardCreate {
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final BankingCardService bankingCardService;

    public BankingAccountCardCreate(
            BankingAccountRepository bankingAccountRepository,
            AuthenticationContext authenticationContext,
            BankingCardService bankingCardService
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.bankingCardService = bankingCardService;
    }

    /**
     * Request a new banking card for the given banking account.
     *
     * @param bankingAccountId
     * @param request
     * @return the created BankingCard
     */
    public BankingCard execute(Long bankingAccountId, BankingAccountCardRequest request) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // we get the BankingAccount to associate the card created.
        final BankingAccount bankingAccount = bankingAccountRepository
                .findById(bankingAccountId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(bankingAccountId)
                );

        // if the logged customer is not admin
        if (!currentCustomer.isAdmin()) {
            // check if the account belongs to this customer.
            bankingAccount.assertOwnedBy(currentCustomer.getId());
        }

        // if customer has reached the maximum amount of cards per account.
        bankingAccount.assertCanAddCard();

        // create the card and associate to the account and return it.
        return bankingCardService.createBankingCard(bankingAccount, request.type());
    }
}