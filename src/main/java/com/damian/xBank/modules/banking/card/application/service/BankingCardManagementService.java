package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockStatusRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.application.guard.BankingCardGuard;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardLockStatus;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

// TODO review this class
@Service
public class BankingCardManagementService {

    private final BankingCardRepository bankingCardRepository;

    public BankingCardManagementService(
            BankingCardRepository bankingCardRepository
    ) {
        this.bankingCardRepository = bankingCardRepository;
    }


    // set the lock status of the card
    private BankingCard updateLockStatus(
            BankingCard card,
            BankingCardLockStatus cardLockStatus
    ) {
        // we mark the card as locked
        card.setLockStatus(cardLockStatus);

        // we change the updateAt timestamp field
        card.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingCardRepository.save(card);
    }

    // (admin) set the lock status of the card.
    public BankingCard updateLockStatus(
            Long bankingCardId,
            BankingCardLockStatus cardLockStatus
    ) {
        // Banking card to set lock status
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        return this.updateLockStatus(bankingCard, cardLockStatus);
    }

    // set the lock status of the card for customers logged
    public BankingCard updateLockStatus(
            Long bankingCardId,
            BankingCardUpdateLockStatusRequest request
    ) {
        // Banking account to be closed
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // check if customer is the owner
        BankingCardGuard
                .forCard(bankingCard)
                .assertOwnership(currentCustomer);

        AuthHelper.validatePassword(currentCustomer, request.password());

        return this.updateLockStatus(bankingCard, request.lockStatus());
    }

    // set the limit of the card
    private BankingCard updateDailyLimit(
            BankingCard bankingCard,
            BigDecimal dailyLimit
    ) {
        // we set the limit of the card
        bankingCard.setDailyLimit(dailyLimit);

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingCard
        return bankingCardRepository.save(bankingCard);
    }

    // (admin) set the limit of the card
    public BankingCard updateDailyLimit(
            Long bankingCardId,
            BigDecimal dailyLimit
    ) {
        // Banking card to set limit
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        return this.updateDailyLimit(bankingCard, dailyLimit);
    }

    // set the limit of the card for customers logged
    public BankingCard updateDailyLimit(
            Long bankingCardId,
            BankingCardUpdateDailyLimitRequest request
    ) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking card to be closed
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // check if customer is the owner
        BankingCardGuard
                .forCard(bankingCard)
                .assertOwnership(currentCustomer);

        AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());

        return this.updateDailyLimit(bankingCard, request.dailyLimit());
    }

    // set the pin
    private BankingCard updatePin(BankingCard bankingCard, String pin) {
        // we set the new pin
        bankingCard.setCardPin(pin);

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingCardRepository.save(bankingCard);
    }

    // (admin) set the pin
    public BankingCard updatePin(Long bankingCardId, String pin) {
        // Banking card to set pin
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        return this.updatePin(bankingCard, pin);
    }

    // set the pin for customers logged
    public BankingCard updatePin(Long bankingCardId, BankingCardUpdatePinRequest request) {
        // Customer logged
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Banking card to set pin on
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(
                        Exceptions.BANKING.CARD.NOT_FOUND, bankingCardId
                ));

        // check if customer is the owner
        BankingCardGuard
                .forCard(bankingCard)
                .assertOwnership(currentCustomer);

        AuthHelper.validatePassword(currentCustomer.getAccount(), request.password());

        return this.updatePin(bankingCard, request.pin());
    }
}
