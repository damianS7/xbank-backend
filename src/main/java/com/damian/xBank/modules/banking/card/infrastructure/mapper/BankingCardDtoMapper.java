package com.damian.xBank.modules.banking.card.infrastructure.mapper;

import com.damian.xBank.modules.banking.card.application.cqrs.result.BankingCardResult;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;

import java.util.Set;
import java.util.stream.Collectors;

public class BankingCardDtoMapper {
    public static BankingCardResult toBankingCardResult(BankingCard bankingCard) {
        return new BankingCardResult(
            bankingCard.getId(),
            bankingCard.getBankingAccount().getId(),
            bankingCard.getHolderName(),
            bankingCard.getCardNumber(),
            bankingCard.getCardCvv(),
            bankingCard.getCardPin(),
            bankingCard.getDailyLimit(),
            bankingCard.getExpiration().getYear(),
            bankingCard.getExpiration().getMonth(),
            bankingCard.getCardType(),
            bankingCard.getStatus(),
            bankingCard.getCreatedAt(),
            bankingCard.getUpdatedAt()
        );
    }

    public static Set<BankingCardResult> toBankingCardResultSetDTO(Set<BankingCard> bankingCards) {
        return bankingCards.stream().map(
            BankingCardDtoMapper::toBankingCardResult
        ).collect(Collectors.toSet());
    }
}
