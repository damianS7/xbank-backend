package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BankingTransactionCardGet {
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public BankingTransactionCardGet(
            BankingCardRepository bankingCardRepository,
            BankingTransactionRepository bankingTransactionRepository,
            AuthenticationContext authenticationContext
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns a paginated result containing the transactions from a card.
     *
     * @param cardId
     * @param pageable
     * @return
     */
    public Page<BankingTransaction> execute(Long cardId, Pageable pageable) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        BankingCard card = bankingCardRepository
                .findById(cardId)
                .orElseThrow(
                        () -> new BankingAccountNotFoundException(cardId)
                );

        // if the current user is a customer ...
        if (currentCustomer.hasRole(UserAccountRole.CUSTOMER)) {

            // assert account belongs to him
            card.assertOwnedBy(currentCustomer.getId());

        }

        return bankingTransactionRepository.findByBankingCardId(cardId, pageable);
    }
}