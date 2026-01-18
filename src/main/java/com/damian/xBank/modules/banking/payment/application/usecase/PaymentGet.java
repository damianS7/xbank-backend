package com.damian.xBank.modules.banking.payment.application.usecase;


import com.damian.xBank.modules.banking.payment.domain.model.Payment;
import com.damian.xBank.modules.banking.payment.infrastructure.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentGet {
    private final PaymentRepository paymentRepository;

    public PaymentGet(
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
    }

    /**
     *
     * @param
     * @return
     */
    public Payment execute(Long id) {

        return paymentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Payment not found " + id)
        );

    }
}