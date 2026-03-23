package com.damian.xBank.modules.banking.account.application.usecase.request;

import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;

public record RequestCardCommand(
    Long bankingAccountId,
    BankingCardType type
) {
}
