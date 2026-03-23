package com.damian.xBank.modules.banking.card.application.usecase.lock;

public record LockBankingCardCommand(
    Long cardId,
    String password
) {
}
