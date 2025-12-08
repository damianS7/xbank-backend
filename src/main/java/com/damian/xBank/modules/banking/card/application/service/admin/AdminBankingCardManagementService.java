package com.damian.xBank.modules.banking.card.application.service.admin;

import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AdminBankingCardManagementService {

    private final BankingCardRepository bankingCardRepository;

    public AdminBankingCardManagementService(
            BankingCardRepository bankingCardRepository
    ) {
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Disable a BankingCard.
     *
     * @param bankingCardId the banking card id
     * @return BankingCard the disabled card
     */
    public BankingCard disableCard(Long bankingCardId) {
        // Banking card to cancel
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // we mark the card as disabled
        bankingCard.setCardStatus(BankingCardStatus.DISABLED);

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingCard
        return bankingCardRepository.save(bankingCard);
    }
}
