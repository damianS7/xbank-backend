package com.damian.xBank.modules.payment.intent.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class PaymentIntentNotFoundException extends ApplicationException {
    public PaymentIntentNotFoundException(Long paymentId) {
        super(ErrorCodes.BANKING_PAYMENT_NOT_FOUND, paymentId, new Object[]{});
    }

}
