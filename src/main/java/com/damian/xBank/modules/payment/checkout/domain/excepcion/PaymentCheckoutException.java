package com.damian.xBank.modules.payment.checkout.domain.excepcion;

import com.damian.xBank.shared.exception.ApplicationException;

public class PaymentCheckoutException extends ApplicationException {
    public PaymentCheckoutException(Object paymentId, String message) {
        super(message, paymentId, new Object[]{});
    }
}
