package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockStatusRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.guard.BankingCardGuard;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BankingCardManagementService {

    private final BankingCardRepository bankingCardRepository;

    public BankingCardManagementService(
            BankingCardRepository bankingCardRepository
    ) {
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Update the lock status of the customer card.
     *
     * @param bankingCardId the banking card id
     * @param request       the request with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    public BankingCard updateLockStatus(
            Long bankingCardId,
            BankingCardUpdateLockStatusRequest request
    ) {
        // Banking card to be updated
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // run validations if not admin
        if (!AuthHelper.isAdmin(currentCustomer)) {

            BankingCardGuard
                    .forCard(bankingCard)
                    .assertOwnership(currentCustomer);

            AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());
        }

        // we mark the card as locked
        bankingCard.setLockStatus(request.lockStatus());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingCardRepository.save(bankingCard);
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
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking card to set limit on
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // run validations if not admin
        if (!AuthHelper.isAdmin(currentCustomer)) {

            BankingCardGuard
                    .forCard(bankingCard)
                    .assertOwnership(currentCustomer);

            AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());
        }

        // we set the limit of the card
        bankingCard.setDailyLimit(request.dailyLimit());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingCard
        return bankingCardRepository.save(bankingCard);
    }

    /**
     * Updates card pin
     *
     * @param bankingCardId the banking card id
     * @param request       the request with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    public BankingCard updatePin(Long bankingCardId, BankingCardUpdatePinRequest request) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking card to set pin on
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // run validations if not admin
        if (!AuthHelper.isAdmin(currentCustomer)) {

            BankingCardGuard
                    .forCard(bankingCard)
                    .assertOwnership(currentCustomer);

            AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());
        }

        // we set the new pin
        bankingCard.setCardPin(request.pin());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingCardRepository.save(bankingCard);
    }
}
