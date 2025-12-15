package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.guard.BankingCardGuard;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BankingCardManagementService {

    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;
    private final AuthenticationContext authenticationContext;

    public BankingCardManagementService(
            PasswordValidator passwordValidator,
            BankingCardRepository bankingCardRepository,
            AuthenticationContext authenticationContext
    ) {
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
        this.authenticationContext = authenticationContext;
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
            BankingCardUpdateLockRequest request
    ) {
        // Banking card to be updated
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // run validations if not admin
        if (!currentCustomer.isAdmin()) {

            BankingCardGuard
                    .forCard(bankingCard)
                    .assertOwnership(currentCustomer);

            passwordValidator.validatePassword(currentCustomer.getAccount(), request.password());
        }

        BankingCardStatus nextStatus = BankingCardStatus.ACTIVE;

        if (bankingCard.getStatus() == BankingCardStatus.ACTIVE) {
            nextStatus = BankingCardStatus.LOCKED;
        }

        // validate card status transition
        bankingCard.getStatus().validateTransition(nextStatus);

        // we mark the card as locked
        bankingCard.setCardStatus(nextStatus);

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
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking card to set limit on
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // run validations if not admin
        if (!currentCustomer.isAdmin()) {

            BankingCardGuard
                    .forCard(bankingCard)
                    .assertOwnership(currentCustomer);

            passwordValidator.validatePassword(currentCustomer.getAccount(), request.password());
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
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking card to set pin on
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // run validations if not admin
        if (!currentCustomer.isAdmin()) {

            BankingCardGuard
                    .forCard(bankingCard)
                    .assertOwnership(currentCustomer);

            passwordValidator.validatePassword(currentCustomer.getAccount(), request.password());
        }

        // we set the new pin
        bankingCard.setCardPin(request.pin());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingCardRepository.save(bankingCard);
    }
}
