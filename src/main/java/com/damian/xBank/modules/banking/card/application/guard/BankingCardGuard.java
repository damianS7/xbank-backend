package com.damian.xBank.modules.banking.card.application.guard;

import com.damian.xBank.modules.banking.card.domain.enums.BankingCardLockStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardAuthorizationException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardInsufficientFundsException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardOwnershipException;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;

import java.math.BigDecimal;

public class BankingCardGuard {
    private final BankingCard card;

    public BankingCardGuard(BankingCard card) {
        this.card = card;
    }

    public static BankingCardGuard forCard(BankingCard card) {
        return new BankingCardGuard(card);
    }

    /**
     * Validate the ownership of the {@link #card}.
     *
     * @param customer the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingCardOwnershipException if the card does not belong to the customer
     */
    public BankingCardGuard ownership(Customer customer) {

        // compare card owner id with given customer id
        if (!card.getOwner().getId().equals(customer.getId())) {
            throw new BankingCardOwnershipException(
                    Exceptions.BANKING.CARD.OWNERSHIP, card.getId(), customer.getId()
            );
        }

        return this;
    }

    /**
     * Validate {@link #card} is not CLOSED or SUSPENDED.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardException if the card does not belong to the customer
     */
    public BankingCardGuard active() {

        // check card status
        final boolean isCardDisabled = card.getCardStatus().equals(BankingCardStatus.DISABLED);

        if (isCardDisabled) {
            throw new BankingCardAuthorizationException(
                    Exceptions.BANKING.CARD.DISABLED, card.getId(), 0L // TODO
            );
        }

        // check lock status
        final boolean isCardLocked = card.getLockStatus().equals(BankingCardLockStatus.LOCKED);
        if (isCardLocked) {
            throw new BankingCardAuthorizationException(
                    Exceptions.BANKING.CARD.LOCKED, card.getId(), 0L // TODO
            );
        }

        return this;
    }

    /**
     * Validate {@link #card} PIN.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardException if the card does not belong to the customer
     */
    public BankingCardGuard PIN(String PIN) {

        // check card pin
        if (!card.getCardPin().equals(PIN)) {
            throw new BankingCardAuthorizationException(
                    Exceptions.BANKING.CARD.INVALID_PIN, card.getId(), 0L // TODO
            );
        }

        return this;
    }

    /**
     * Validate if the {@link #card} has sufficient funds.
     *
     * @param amount the amount to check
     * @return the current validator instance for chaining
     * @throws BankingCardOwnershipException if the card does not belong to the customer
     */
    public BankingCardGuard sufficientFunds(BigDecimal amount) {

        // check if card has enough funds
        if (!card.hasEnoughFundsToSpend(amount)) {
            throw new BankingCardInsufficientFundsException(
                    Exceptions.BANKING.CARD.INSUFFICIENT_FUNDS,
                    card.getId()
            );
        }

        return this;
    }

    public void canSpend(
            Customer customer,
            String cardPIN,
            BigDecimal amount
    ) {
        this.ownership(customer)
            .active()
            .sufficientFunds(amount)
            .PIN(cardPIN);

    }
}