package com.damian.xBank.modules.banking.payment.domain.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class PaymentNotFoundException extends ApplicationException {
    public PaymentNotFoundException(Long paymentId) {
        super(ErrorCodes.BANKING_PAYMENT_NOT_FOUND, paymentId, new Object[]{});
    }

}
