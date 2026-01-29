package com.damian.xBank.modules.payment.intent.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class PaymentIntentNotPendingException extends ApplicationException {
    public PaymentIntentNotPendingException(Long paymentId) {
        super(ErrorCodes.PAYMENT_INTENT_NOT_PENDING, paymentId, new Object[]{});
    }

}
