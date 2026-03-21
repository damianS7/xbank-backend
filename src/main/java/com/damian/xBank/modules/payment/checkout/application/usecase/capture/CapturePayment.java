package com.damian.xBank.modules.payment.checkout.application.usecase.capture;


import com.damian.xBank.modules.payment.intent.domain.exception.PaymentIntentNotFoundException;
import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import com.damian.xBank.modules.payment.intent.infrastructure.repository.PaymentIntentRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde el merchant captura los fondos de un pago Autorizado.
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
     * @param command
     * @return
     */
    @Transactional
    public PaymentIntent execute(CapturePaymentCommand command) {

        // 1. Buscar el payment intent
        PaymentIntent paymentIntent = paymentIntentRepository
            .findById(command.paymentId())
            .orElseThrow(
                () -> new PaymentIntentNotFoundException(command.paymentId())
            );

        // 2. Deducir fondos de la tarjeta del usuario ??? o en este paso ya deberia estar hecho
        //                payment.getUser().getAccount().add


        // 3. Agregar fondos al merchant

        // Completar transaccion?
        //        paymentIntent.capture();

        return paymentIntentRepository.save(paymentIntent);
    }
}