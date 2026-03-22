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
 * Caso de uso donde el payment-gateway intenta autorizar un pago.
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
     * Autoriza un pago con tarjeta.
     *
     * @param command datos necesarios para autorizar el pago
     * @return resultado de la autorización del pago
     */
    // TODO cambiar PaymentAuthorizationResponse a result???
    public PaymentAuthorizationResponse execute(AuthorizeCardPaymentCommand command) {
        // Buscar la tarjeta
        BankingCard bankingCard = bankingCardRepository
            .findByCardNumber(CardNumber.of(command.cardNumber()))
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardNumber()));

        // Comprobar que la tarjeta puede realizar el pago
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

        // Autoriza y guarda la transacción
        transaction.authorize();
        transaction = bankingTransactionRepository.save(transaction);

        return new PaymentAuthorizationResponse(
            PaymentAuthorizationStatus.AUTHORIZED,
            transaction.getAuthorizationId(),
            null
        );
    }
}