package com.damian.xBank.modules.banking.card.application.usecase.activate;

public record ActivateBankingCardCommand(
    Long cardId,
    String cvv
) {
}
