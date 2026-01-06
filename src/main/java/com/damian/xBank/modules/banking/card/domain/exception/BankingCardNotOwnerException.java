package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardNotOwnerException extends BankingCardException {
    public BankingCardNotOwnerException(Long bankingCardId, Long unAuthorizedCustomerId) {
        super(ErrorCodes.BANKING_CARD_NOT_OWNER, bankingCardId, new Object[]{unAuthorizedCustomerId});
    }
}
