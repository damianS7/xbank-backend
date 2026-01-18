package com.damian.xBank.modules.banking.payment.application.usecase;


import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.payment.domain.exception.PaymentNotFoundException;
import com.damian.xBank.modules.banking.payment.domain.model.Payment;
import com.damian.xBank.modules.banking.payment.domain.model.PaymentStatus;
import com.damian.xBank.modules.banking.payment.infrastructure.repository.PaymentRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentSubmit {
    private final NotificationEventFactory notificationEventFactory;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;
    private final BankingCardRepository bankingCardRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationPublisher notificationPublisher;

    public PaymentSubmit(
            NotificationEventFactory notificationEventFactory,
            BankingTransactionPersistenceService bankingTransactionPersistenceService,
            BankingCardRepository bankingCardRepository,
            PaymentRepository paymentRepository,
            NotificationPublisher notificationPublisher
    ) {
        this.notificationEventFactory = notificationEventFactory;
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.bankingCardRepository = bankingCardRepository;
        this.paymentRepository = paymentRepository;
        this.notificationPublisher = notificationPublisher;
    }

    /**
     *
     * @param
     * @return
     */
    @Transactional
    public Payment execute(Long paymentId, String cardNumber, String cardCvv, String cardPin) {
        BankingCard card = bankingCardRepository
                .findByCardNumber(cardNumber)
                .orElseThrow(
                        () -> new BankingCardNotFoundException(cardNumber)
                );

        card.assertUsable();
        card.assertCorrectPin(cardPin);
        card.assertCorrectCvv(cardCvv);

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentNotFoundException(paymentId)
        );

        card.assertSufficientFunds(payment.getAmount());


        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.CARD_CHARGE,
                        card.getBankingAccount(),
                        payment.getAmount()
                )
                .setBankingCard(card)
                .setStatus(BankingTransactionStatus.COMPLETED)
                .setDescription(payment.getMerchant());

        card.chargeAmount(payment.getAmount());

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);

        payment.setStatus(PaymentStatus.COMPLETED);
        
        // Notify the user
        notificationPublisher.publish(
                notificationEventFactory.cardPaymentCompleted(transaction)
        );

        return paymentRepository.save(payment);
    }
}