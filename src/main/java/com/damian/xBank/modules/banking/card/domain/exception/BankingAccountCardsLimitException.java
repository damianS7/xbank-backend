package com.damian.xBank.modules.banking.card.domain.exception;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class BankingAccountCardsLimitException extends BankingAccountException {
    public BankingAccountCardsLimitException(Long bankingAccountId) {
        super(ErrorCodes.BANKING_ACCOUNT_CARD_LIMIT, bankingAccountId);
    }

}