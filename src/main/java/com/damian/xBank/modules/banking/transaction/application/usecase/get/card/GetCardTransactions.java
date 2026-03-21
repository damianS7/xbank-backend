package com.damian.xBank.modules.banking.transaction.application.usecase.get.card;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para obtener las transacciones de una tarjeta
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
     * @param query Datos de la consulta
     * @return Result con las transacciones paginadas
     */
    public PageResult<BankingTransactionResult> execute(GetCardTransactionsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        final BankingCard card = bankingCardRepository
            .findById(query.cardId())
            .orElseThrow(() -> new BankingAccountNotFoundException(query.cardId()));

        // Si no es admin se comprueba que es el dueño
        if (!currentUser.isAdmin()) {
            card.assertOwnedBy(currentUser.getId());
        }

        Page<BankingTransaction> pagedResult = bankingTransactionRepository
            .findByBankingCard_Id(query.cardId(), query.pageable());

        return PageResult.fromPagedTransactions(pagedResult);
    }
}