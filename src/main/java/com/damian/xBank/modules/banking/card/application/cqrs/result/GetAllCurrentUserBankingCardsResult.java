package com.damian.xBank.modules.banking.card.application.cqrs.result;

import java.util.Set;

public record GetAllCurrentUserBankingCardsResult(
    Set<BankingCardResult> cards
) {
}
