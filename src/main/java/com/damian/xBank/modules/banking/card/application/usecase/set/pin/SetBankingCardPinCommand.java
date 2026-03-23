package com.damian.xBank.modules.banking.card.application.usecase.set.pin;

public record SetBankingCardPinCommand(
    Long cardId,
    String pin,
    String password
) {
}
