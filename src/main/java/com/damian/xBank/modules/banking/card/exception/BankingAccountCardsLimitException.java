package com.damian.xBank.modules.banking.card.exception;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountException;
import com.damian.xBank.shared.exception.Exceptions;

public class BankingAccountCardsLimitException extends BankingAccountException {
    public BankingAccountCardsLimitException(Long bankingAccountId) {
        this(Exceptions.BANKING.ACCOUNT.CARD_LIMIT, bankingAccountId);
    }

    public BankingAccountCardsLimitException(String message, Long bankingAccountId) {
        super(message, bankingAccountId);
    }
}