package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardAuthorizeRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import org.springframework.stereotype.Service;

/**
 * This usecase its used by the payment-gateway to check if the card its authorized to
 * carry the operation.
 */
@Service
public class BankingCardAuthorize {
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;

    public BankingCardAuthorize(
            BankingCardRepository bankingCardRepository,
            BankingTransactionPersistenceService bankingTransactionPersistenceService
    ) {
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Spend money from a card
     *
     * @param request the request with the data needed to perfom the operation
     * @return the created transaction
     */
    public void execute(BankingCardAuthorizeRequest request) {
        // check card exists
        BankingCard bankingCard = bankingCardRepository
                .findByCardNumber(request.cardNumber())
                .orElseThrow(
                        () -> new BankingCardNotFoundException(request.cardNumber())
                );

        // its active
        bankingCard.assertUsable();

        // has funds
        bankingCard.assertSufficientFunds(request.amount());

        // check security
        // TODO assertExpiryDates assertExpiration
        bankingCard.assertCorrectPin(request.pin());
        bankingCard.assertCorrectCvv(request.cvv());

        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.CARD_CHARGE,
                        bankingCard.getBankingAccount(),
                        request.amount()
                )
                .setBankingCard(bankingCard)
                .setStatus(BankingTransactionStatus.PENDING)
                .setDescription(request.merchantName());

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);
    }
}