package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BankingCardGetAll {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public BankingCardGetAll(
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }

    // return the cards of the logged customer
    public Set<BankingCard> execute() {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        return bankingCardRepository.findCardsByCustomerId(currentCustomer.getId());
    }
}