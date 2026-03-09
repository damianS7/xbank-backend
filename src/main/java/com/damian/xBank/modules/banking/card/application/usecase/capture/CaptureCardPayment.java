package com.damian.xBank.modules.banking.card.application.usecase.capture;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment network will use this to capture merchant authorized funds
 * TODO: Maybe this is not the place? Move/Add to payment network module?
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
     * Capture the authorized amount and mark transaction as completed
     *
     * @param command
     */
    @Transactional
    public CaptureCardPaymentResult execute(CaptureCardPaymentCommand command) {
        // check transaction exists
        BankingTransaction transaction = bankingTransactionRepository
            .findById(command.authorizationId())
            .orElseThrow(
                () -> new BankingTransactionNotFoundException(command.authorizationId())
            );

        // mark as captured
        transaction.capture();

        // deduct from card
        BankingCard card = transaction.getBankingCard();
        card.chargeAmount(transaction.getAmount());

        // Notify the user
        //        notificationPublisher.publish(
        //                notificationEventFactory.cardPaymentCompleted(transaction)
        //        );

        bankingTransactionRepository.save(transaction);

        return CaptureCardPaymentResult.from(transaction);
    }
}