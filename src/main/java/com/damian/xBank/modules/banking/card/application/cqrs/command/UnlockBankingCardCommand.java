package com.damian.xBank.modules.banking.card.application.cqrs.command;

public record UnlockBankingCardCommand(
    Long cardId,
    String password
) {
}
