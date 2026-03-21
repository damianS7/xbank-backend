package com.damian.xBank.modules.banking.card.application.usecase.capture;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde PaymentNetwork intenta capturar los fondos pertenecientes a un merchant.
 */
@Service
public class CaptureCardPayment {
    private final BankingTransactionRepository bankingTransactionRepository;

    public CaptureCardPayment(
        BankingTransactionRepository bankingTransactionRepository
    ) {
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    /**
     * @param command comando con los datos necesarios
     */
    @Transactional
    public CaptureCardPaymentResult execute(CaptureCardPaymentCommand command) {
        // Buscar la transacción asociada
        BankingTransaction transaction = bankingTransactionRepository
            .findByAuthorizationId(command.authorizationId())
            .orElseThrow(() -> new BankingTransactionNotFoundException(command.authorizationId()));

        // Tarjeta asociada a la transacción
        BankingCard card = transaction.getBankingCard();

        // Capturar fondos
        card.capture(transaction);
        bankingTransactionRepository.save(transaction);

        return CaptureCardPaymentResult.from(transaction);
    }
}