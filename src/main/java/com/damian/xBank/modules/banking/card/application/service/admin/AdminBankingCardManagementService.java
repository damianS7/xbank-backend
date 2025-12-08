package com.damian.xBank.modules.banking.card.application.service.admin;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockStatusRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateStatusRequest;
import com.damian.xBank.modules.banking.card.application.service.BankingCardManagementService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AdminBankingCardManagementService {

    private final BankingCardRepository bankingCardRepository;
    private final BankingCardManagementService bankingCardManagementService;

    public AdminBankingCardManagementService(
            BankingCardRepository bankingCardRepository,
            BankingCardManagementService bankingCardManagementService
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingCardManagementService = bankingCardManagementService;
    }

    /**
     * Updates the card status.
     *
     * @param bankingCardId the banking card id
     * @return BankingCard with updated status
     */
    public BankingCard updateStatus(
            Long bankingCardId,
            BankingCardUpdateStatusRequest request
    ) {
        // Banking card to cancel
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // we mark the card as disabled
        bankingCard.setCardStatus(request.status());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingCard
        return bankingCardRepository.save(bankingCard);
    }

    /**
     * Update the lock status of the card.
     *
     * @param bankingCardId the banking card id
     * @param request       the request with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    public BankingCard updateLockStatus(
            Long bankingCardId,
            BankingCardUpdateLockStatusRequest request
    ) {
        return bankingCardManagementService.updateLockStatus(bankingCardId, request);
    }

    /**
     * Update the daily limit of the card.
     *
     * @param bankingCardId the banking card id
     * @param request       the request with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    public BankingCard updateDailyLimit(
            Long bankingCardId,
            BankingCardUpdateDailyLimitRequest request
    ) {
        return bankingCardManagementService.updateDailyLimit(bankingCardId, request);
    }

    /**
     * Updates card pin
     *
     * @param bankingCardId the banking card id
     * @param request       the request with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    public BankingCard updatePin(Long bankingCardId, BankingCardUpdatePinRequest request) {
        return bankingCardManagementService.updatePin(bankingCardId, request);
    }
}
