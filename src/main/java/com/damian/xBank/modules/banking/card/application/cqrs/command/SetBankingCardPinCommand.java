package com.damian.xBank.modules.banking.card.application.cqrs.command;

public record SetBankingCardPinCommand(
    Long cardId,
    String pin,
    String password
) {
}
