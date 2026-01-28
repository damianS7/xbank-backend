package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.CaptureCardPaymentRequest;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment network will use this to capture merchant authorized funds
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
     * @param request
     */
    @Transactional
    public BankingTransaction execute(CaptureCardPaymentRequest request) {
        // check transaction exists
        BankingTransaction transaction = bankingTransactionRepository
                .findById(request.authorizationId())
                .orElseThrow(
                        () -> new BankingTransactionNotFoundException(request.authorizationId())
                );

        // deduct from card
        BankingCard card = transaction.getBankingCard();
        card.chargeAmount(transaction.getAmount());

        // mark as captured
        transaction.capture();

        // Notify the user
        //        notificationPublisher.publish(
        //                notificationEventFactory.cardPaymentCompleted(transaction)
        //        );

        return bankingTransactionRepository.save(transaction);
    }
}