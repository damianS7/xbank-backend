package com.damian.xBank.modules.banking.card.application.mapper;

import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;

import java.util.Set;
import java.util.stream.Collectors;

public class BankingCardDtoMapper {
    public static BankingCardDto toBankingCardDto(BankingCard bankingCard) {
        return new BankingCardDto(
                bankingCard.getId(),
                bankingCard.getBankingAccount().getId(),
                bankingCard.getHolderName(),
                bankingCard.getCardNumber(),
                bankingCard.getCardCvv(),
                bankingCard.getCardPin(),
                bankingCard.getDailyLimit(),
                bankingCard.getExpiredDate(),
                bankingCard.getCardType(),
                bankingCard.getStatus(),
                bankingCard.getCreatedAt(),
                bankingCard.getUpdatedAt()
        );
    }

    public static Set<BankingCardDto> toBankingCardSetDTO(Set<BankingCard> bankingCards) {
        return bankingCards.stream().map(
                BankingCardDtoMapper::toBankingCardDto
        ).collect(Collectors.toSet());
    }
}
