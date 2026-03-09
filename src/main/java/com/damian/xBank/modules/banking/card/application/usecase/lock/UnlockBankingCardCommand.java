package com.damian.xBank.modules.banking.card.application.usecase.lock;

public record UnlockBankingCardCommand(
    Long cardId,
    String password
) {
}
