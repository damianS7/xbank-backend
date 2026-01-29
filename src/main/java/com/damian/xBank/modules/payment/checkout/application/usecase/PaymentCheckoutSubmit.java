package com.damian.xBank.modules.payment.checkout.application.usecase;


import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.factory.NotificationEventFactory;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.payment.checkout.application.dto.request.PaymentCheckoutSubmitRequest;
import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.payment.network.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.network.application.dto.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.network.domain.PaymentAuthorizationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This usecase will be used after user submits the checkout form.
 * <p>
 * When the form is submitted this class method will be called.
 */
@Service
public class PaymentCheckoutSubmit {
    private final NotificationEventFactory notificationEventFactory;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;
    private final PaymentNetworkGateway paymentNetworkGateway;
    private final PaymentIntentRepository paymentIntentRepository;
    private final NotificationPublisher notificationPublisher;

    public PaymentCheckoutSubmit(
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

    /**
     *
     * @param request
     * @return PaymentIntent
     */
    @Transactional
    public PaymentIntent execute(PaymentCheckoutSubmitRequest request) {
        PaymentIntent paymentIntent = paymentIntentRepository.findById(request.paymentId()).orElseThrow(
                () -> new PaymentIntentNotFoundException(request.paymentId())
        );

        // Payment must be pending
        paymentIntent.assertPending();

        // Send a request to the payment network to authorize this payment
        PaymentAuthorizationResponse response = paymentNetworkGateway.authorizePayment(
                request.cardNumber(),
                request.cardCvv(),
                request.cardPin(),
                paymentIntent.getAmount(),
                paymentIntent.getMerchantName()
        );

        if (response.status() != PaymentAuthorizationStatus.AUTHORIZED) {
            throw new RuntimeException("Payment authorization failed");
        }

        // If the payment is authorized we should send to the card issuer bank
        // Or the payment network should send to the issuer card?
        paymentIntent.authorize();

        // Notify the merchant
        //        notificationPublisher.publish(
        //                notificationEventFactory.cardPaymentCompleted(transaction)
        //        );

        // redirect to merchant website
        return paymentIntentRepository.save(paymentIntent);
    }
}