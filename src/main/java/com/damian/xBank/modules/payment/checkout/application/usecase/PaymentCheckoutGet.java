package com.damian.xBank.modules.payment.checkout.application.usecase;


import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import org.springframework.stereotype.Service;

/**
 * This class is used by the customer to get his pending payment.
 */
@Service
public class PaymentCheckoutGet {
    private final PaymentIntentRepository paymentIntentRepository;

    public PaymentCheckoutGet(
            PaymentIntentRepository paymentIntentRepository
    ) {
        this.paymentIntentRepository = paymentIntentRepository;
    }

    /**
     *
     * @param
     * @return
     */
    public PaymentIntent execute(Long id) {

        return paymentIntentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Payment not found " + id)
        );

    }
}