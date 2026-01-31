package com.damian.xBank.modules.payment.intent.application.usecase;


import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import org.springframework.stereotype.Service;

/**
 * This usecase return a payment intent
 */
@Service
public class GetPaymentIntent {
    private final PaymentIntentRepository paymentIntentRepository;

    public GetPaymentIntent(
            PaymentIntentRepository paymentIntentRepository
    ) {
        this.paymentIntentRepository = paymentIntentRepository;
    }

    public PaymentIntent execute(Long id) {
        return paymentIntentRepository.findById(id).orElseThrow(
                () -> new PaymentIntentNotFoundException(id)
        );
    }
}