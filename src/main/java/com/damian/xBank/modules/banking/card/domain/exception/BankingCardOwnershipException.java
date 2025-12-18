package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingCardOwnershipException extends BankingCardException {
    public BankingCardOwnershipException(Long bankingCardId, Long unAuthorizedCustomerId) {
        super(ErrorCodes.BANKING_CARD_OWNERSHIP, bankingCardId, new Object[]{unAuthorizedCustomerId});
    }
}
