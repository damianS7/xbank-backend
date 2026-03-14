package com.damian.xBank.modules.payment.checkout.application.usecase.capture;


import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.modules.payment.intent.infrastructure.rest.request.CapturePaymentRequest;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used by the merchant to capture the funds from an AUTHORIZED payment.
 */
@Service
public class CapturePayment {
    private final PaymentIntentRepository paymentIntentRepository;
    private final AuthenticationContext authenticationContext;

    public CapturePayment(
        PaymentIntentRepository paymentIntentRepository,
        AuthenticationContext authenticationContext
    ) {
        this.paymentIntentRepository = paymentIntentRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     *
     * @param
     * @return
     */
    @Transactional
    public PaymentIntent execute(CapturePaymentRequest request) {

        // 1. find the payment
        PaymentIntent paymentIntent = paymentIntentRepository
            .findById(request.paymentId())
            .orElseThrow(
                () -> new PaymentIntentNotFoundException(request.paymentId())
            );

        // 2. deduce funds from user card
        //                payment.getUser().getAccount().add


        // 3. add the funds to the merchant

        // find transaction and completed it

        //        paymentIntent.setStatus(PaymentIntentStatus.CAPTURED);

        return paymentIntentRepository.save(paymentIntent);
    }
}