package com.damian.xBank.modules.banking.card.application.cqrs.command;

public record ActivateBankingCardCommand(
    Long cardId,
    String cvv
) {
}
