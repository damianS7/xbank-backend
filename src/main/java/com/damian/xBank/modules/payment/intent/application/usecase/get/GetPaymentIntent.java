package com.damian.xBank.modules.payment.intent.application.usecase.get;


import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import org.springframework.stereotype.Service;

/**
 * Caso de uso que devuelve los datos de un payment intent
 */
@Service
public class GetPaymentIntent {
    private final PaymentIntentRepository paymentIntentRepository;

    public GetPaymentIntent(
        PaymentIntentRepository paymentIntentRepository
    ) {
        this.paymentIntentRepository = paymentIntentRepository;
    }

    public GetPaymentIntentResult execute(Long id) {
        PaymentIntent paymentIntent = paymentIntentRepository.findById(id).orElseThrow(
            () -> new PaymentIntentNotFoundException(id)
        );

        return GetPaymentIntentResult.from(paymentIntent);
    }
}