package com.damian.xBank.modules.banking.card.application.usecase.authorize;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import org.springframework.stereotype.Service;

/**
 * This is used by the payment-gateway to check if the card is authorized to
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
     * @param command
     */
    public PaymentAuthorizationResponse execute(AuthorizeCardPaymentCommand command) {
        // check card exists
        BankingCard bankingCard = bankingCardRepository
            .findByCardNumber(command.cardNumber())
            .orElseThrow(
                () -> new BankingCardNotFoundException(command.cardNumber())
            );

        // check security
        bankingCard.authorizePayment(
            command.amount(),
            command.expiryMonth(),
            command.expiryYear(),
            command.cvv()
        );

        BankingTransaction transaction = BankingTransaction
            .create(
                BankingTransactionType.CARD_CHARGE,
                bankingCard.getBankingAccount(),
                command.amount()
            )
            .setBankingCard(bankingCard)
            .setDescription(command.merchant());

        // store here the transaction as AUTHORIZED
        transaction = bankingTransactionPersistenceService.record(transaction);

        return new PaymentAuthorizationResponse(
            PaymentAuthorizationStatus.AUTHORIZED,
            transaction.getId().toString(),
            null
        );
    }
}