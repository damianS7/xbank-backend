package com.damian.xBank.modules.banking.card.application.usecase.get;

import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;

import java.util.Set;

public record GetAllCurrentUserBankingCardsResult(
    Set<BankingCardResult> cards
) {
}
