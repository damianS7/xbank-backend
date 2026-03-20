package com.damian.xBank.modules.banking.card.application.usecase.authorize;

import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.CardNumber;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
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
    private final BankingTransactionRepository bankingTransactionRepository;

    public AuthorizeCardPayment(
        BankingCardRepository bankingCardRepository,
        BankingTransactionRepository bankingTransactionRepository
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    /**
     *
     * @param command
     */
    public PaymentAuthorizationResponse execute(AuthorizeCardPaymentCommand command) {
        // check card exists
        BankingCard bankingCard = bankingCardRepository
            .findByCardNumber(CardNumber.of(command.cardNumber()))
            .orElseThrow(
                () -> new BankingCardNotFoundException(command.cardNumber())
            );

        // check security
        bankingCard.authorize(
            command.amount(),
            command.expiryMonth(),
            command.expiryYear(),
            command.cvv()
        );

        BankingTransaction transaction = BankingTransaction.create(
            BankingTransactionType.CARD_CHARGE,
            bankingCard,
            command.amount(),
            command.merchant()
        );

        // store here the transaction as AUTHORIZED
        transaction = bankingTransactionRepository.save(transaction);

        return new PaymentAuthorizationResponse(
            PaymentAuthorizationStatus.AUTHORIZED,
            transaction.getId().toString(),
            null
        );
    }
}