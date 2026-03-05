package com.damian.xBank.modules.payment.checkout.application.usecase;

import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.payment.checkout.application.cqrs.command.SubmitPaymentCheckoutCommand;
import com.damian.xBank.modules.payment.checkout.domain.excepcion.PaymentCheckoutException;
import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.payment.network.card.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.card.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.network.card.infrastructure.http.dto.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.network.card.infrastructure.http.dto.response.PaymentAuthorizationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This usecase will be used after user submits the checkout form.
 * <p>
 * When the form is submitted this class method will be called.
 */
@Service
public class SubmitPaymentCheckout {
    private final NotificationEventFactory notificationEventFactory;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;
    private final PaymentNetworkGateway paymentNetworkGateway;
    private final PaymentIntentRepository paymentIntentRepository;
    private final NotificationPublisher notificationPublisher;

    public SubmitPaymentCheckout(
        NotificationEventFactory notificationEventFactory,
        BankingTransactionPersistenceService bankingTransactionPersistenceService,
        PaymentNetworkGateway paymentNetworkGateway,
        PaymentIntentRepository paymentIntentRepository,
        NotificationPublisher notificationPublisher
    ) {
        this.notificationEventFactory = notificationEventFactory;
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
        this.paymentNetworkGateway = paymentNetworkGateway;
        this.paymentIntentRepository = paymentIntentRepository;
        this.notificationPublisher = notificationPublisher;
    }

    @Transactional
    public void execute(SubmitPaymentCheckoutCommand command) {
        PaymentIntent paymentIntent = paymentIntentRepository.findById(command.paymentId()).orElseThrow(
            () -> new PaymentIntentNotFoundException(command.paymentId())
        );

        // Payment must be pending
        paymentIntent.assertPending();

        PaymentAuthorizationResponse response = paymentNetworkGateway.authorizePayment(
            new PaymentAuthorizationRequest(
                paymentIntent.getMerchantName(),
                command.cardHolder(),
                command.cardNumber(),
                command.expiryMonth(),
                command.expiryYear(),
                command.cardCvv(),
                command.cardPin(),
                paymentIntent.getAmount(),
                paymentIntent.getCurrency().toString(),
                paymentIntent.getDescription()
            )
        );

        if (response.status() != PaymentAuthorizationStatus.AUTHORIZED) {
            throw new PaymentCheckoutException(paymentIntent.getId(), response.declineReason());
        }

        // If the payment is authorized we should send to the card issuer bank
        // Or the payment network should send to the issuer card?
        paymentIntent.authorize();

        // Notify the merchant
        //        notificationPublisher.publish(
        //                notificationEventFactory.cardPaymentCompleted(transaction)
        //        );

        // redirect to merchant website
        paymentIntentRepository.save(paymentIntent);
    }
}