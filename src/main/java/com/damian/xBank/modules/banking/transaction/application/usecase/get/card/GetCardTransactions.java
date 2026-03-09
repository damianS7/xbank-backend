package com.damian.xBank.modules.banking.transaction.application.usecase.get.card;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Use case for retrieving the transactions of a banking card.
 */
@Service
public class GetCardTransactions {
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionRepository bankingTransactionRepository;
    private final AuthenticationContext authenticationContext;

    public GetCardTransactions(
        BankingCardRepository bankingCardRepository,
        BankingTransactionRepository bankingTransactionRepository,
        AuthenticationContext authenticationContext
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     *
     * @param query
     * @return a result containing the paged transactions.
     */
    public PageResult<BankingTransactionResult> execute(GetCardTransactionsQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingCard card = bankingCardRepository
            .findById(query.cardId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(query.cardId())
            );

        // if the current user is a customer ...
        if (currentUser.hasRole(UserRole.CUSTOMER)) {

            // assert account belongs to him
            card.assertOwnedBy(currentUser.getId());

        }

        Page<BankingTransaction> pagedResult = bankingTransactionRepository
            .findByBankingCard_Id(
                query.cardId(),
                query.pageable()
            );

        return PageResult.from(pagedResult);
    }
}