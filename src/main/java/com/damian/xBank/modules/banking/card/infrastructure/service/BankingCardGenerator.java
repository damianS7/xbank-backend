package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;

public interface BankingCardGenerator {
    BankingCard generate(BankingAccount bankingAccount, BankingCardType cardType);

    String generateCardNumber();

    String generateCvv();

    String generatePin();
}