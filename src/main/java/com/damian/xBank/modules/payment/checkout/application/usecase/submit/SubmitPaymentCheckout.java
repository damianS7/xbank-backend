package com.damian.xBank.modules.payment.checkout.application.usecase.submit;

import com.damian.xBank.modules.payment.checkout.application.PaymentNetworkGateway;
import com.damian.xBank.modules.payment.checkout.domain.PaymentAuthorizationStatus;
import com.damian.xBank.modules.payment.checkout.domain.excepcion.PaymentCheckoutException;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.request.PaymentAuthorizationRequest;
import com.damian.xBank.modules.payment.checkout.infrastructure.http.response.PaymentAuthorizationResponse;
import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde el usuario hace submit en el formulario de checkout
 */
@Service
public class SubmitPaymentCheckout {
    private final PaymentNetworkGateway paymentNetworkGateway;
    private final PaymentIntentRepository paymentIntentRepository;

    public SubmitPaymentCheckout(
        PaymentNetworkGateway paymentNetworkGateway,
        PaymentIntentRepository paymentIntentRepository
    ) {
        this.paymentNetworkGateway = paymentNetworkGateway;
        this.paymentIntentRepository = paymentIntentRepository;
    }

    @Transactional
    public void execute(SubmitPaymentCheckoutCommand command) {
        PaymentIntent paymentIntent = paymentIntentRepository
            .findById(command.paymentId())
            .orElseThrow(() -> new PaymentIntentNotFoundException(command.paymentId()));

        // El pago debe estar en estado PENDING
        paymentIntent.assertPending();

        PaymentAuthorizationResponse response = paymentNetworkGateway.authorizePayment(
            new PaymentAuthorizationRequest(
                paymentIntent.getMerchant().getMerchantName(),
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

        paymentIntent.authorize();
        paymentIntentRepository.save(paymentIntent);
    }
}