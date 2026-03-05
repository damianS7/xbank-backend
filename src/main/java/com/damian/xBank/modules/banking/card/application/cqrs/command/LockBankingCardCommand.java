package com.damian.xBank.modules.banking.card.application.cqrs.command;

public record LockBankingCardCommand(
    Long cardId,
    String password
) {
}
