package com.damian.xBank.modules.banking.card.application.guard;

import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardLockStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.*;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.exception.Exceptions;

import java.math.BigDecimal;
import java.util.Objects;

public class BankingCardGuard {
    private final BankingCard card;

    public BankingCardGuard(BankingCard card) {
        this.card = card;
    }

    public static BankingCardGuard forCard(BankingCard card) {
        return new BankingCardGuard(card);
    }

    /**
     * Assert the ownership of the {@link #card} belongs to {@link Customer}.
     *
     * @param customer the customer to check ownership against
     * @return the current validator instance for chaining
     * @throws BankingCardOwnershipException if the card does not belong to the customer
     */
    public BankingCardGuard assertOwnership(Customer customer) {

        // compare card owner id with given customer id
        if (!Objects.equals(card.getOwner().getId(), customer.getId())) {
            throw new BankingCardOwnershipException(
                    Exceptions.BANKING.CARD.OWNERSHIP, card.getId(), customer.getId()
            );
        }

        return this;
    }

    /**
     * Assert {@link #card} is not DISABLED or LOCKED and can be used for any operation.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardException if the card does not belong to the customer
     */
    public BankingCardGuard assertUsable() {

        this.assertEnabled()
            .assertUnlocked();

        return this;
    }

    /**
     * Assert {@link #card} is not DISABLED.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardDisabledException if the card is locked
     */
    public BankingCardGuard assertEnabled() {

        // check card status
        if (Objects.equals(card.getCardStatus(), BankingCardStatus.DISABLED)) {
            throw new BankingCardDisabledException(
                    Exceptions.BANKING.CARD.DISABLED, card.getId()
            );
        }

        return this;
    }

    /**
     * Assert {@link #card} is not LOCKED.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardLockedException if the card is locked
     */
    public BankingCardGuard assertUnlocked() {

        // check lock status
        if (Objects.equals(card.getLockStatus(), BankingCardLockStatus.LOCKED)) {
            throw new BankingCardLockedException(
                    Exceptions.BANKING.CARD.LOCKED, card.getId()
            );
        }

        return this;
    }

    /**
     * Assert the {@link #card} PIN matches.
     *
     * @return the current validator instance for chaining
     * @throws BankingCardInvalidPinException if the card PIN does not equals to the given PIN
     */
    public BankingCardGuard assertCorrectPin(String PIN) {

        // check card pin
        if (!Objects.equals(card.getCardPin(), PIN)) {
            throw new BankingCardInvalidPinException(
                    Exceptions.BANKING.CARD.INVALID_PIN, card.getId()
            );
        }

        return this;
    }

    /**
     * Assert the {@link #card} has sufficient funds.
     *
     * @param amount the amount to check
     * @return the current validator instance for chaining
     * @throws BankingCardOwnershipException if the card does not belong to the customer
     */
    public BankingCardGuard assertSufficientFunds(BigDecimal amount) {

        // check if card has enough funds
        if (!card.hasEnoughFundsToSpend(amount)) {
            throw new BankingCardInsufficientFundsException(
                    Exceptions.BANKING.CARD.INSUFFICIENT_FUNDS,
                    card.getId()
            );
        }

        return this;
    }

    /**
     * Assert the {@link #card} has can spend funds.
     * It checks card ownership, and, is usable, has funds, and has correct PIN.
     *
     * @param customer the customer to check ownership
     * @param cardPIN  the card pin to check
     * @param amount   the amount to check funds
     */
    public void assertCanSpend(
            Customer customer,
            String cardPIN,
            BigDecimal amount
    ) {
        this.assertOwnership(customer)
            .assertUsable()
            .assertSufficientFunds(amount)
            .assertCorrectPin(cardPIN);
    }
}