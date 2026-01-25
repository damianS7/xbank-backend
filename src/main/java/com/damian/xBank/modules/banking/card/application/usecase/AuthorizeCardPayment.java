package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.AuthorizeCardPaymentRequest;
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
public class AuthorizeCardPayment {
    private final BankingCardRepository bankingCardRepository;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;

    public AuthorizeCardPayment(
            BankingCardRepository bankingCardRepository,
            BankingTransactionPersistenceService bankingTransactionPersistenceService
    ) {
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     *
     * @param request
     */
    public void execute(AuthorizeCardPaymentRequest request) {
        // check card exists
        BankingCard bankingCard = bankingCardRepository
                .findByCardNumber(request.cardNumber())
                .orElseThrow(
                        () -> new BankingCardNotFoundException(request.cardNumber())
                );

        // check security
        bankingCard.authorizePayment(
                request.amount(),
                request.expiryMonth(),
                request.expiryYear(),
                request.pin(),
                request.cvv()
        );

        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.CARD_CHARGE,
                        bankingCard.getBankingAccount(),
                        request.amount()
                )
                .setBankingCard(bankingCard)
                .setStatus(BankingTransactionStatus.PENDING) // TODO> AUTHORIZED?
                .setDescription(request.merchantName());
        // TODO add FK to paymentId?

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);
    }
}