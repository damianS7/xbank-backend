package com.damian.xBank.modules.banking.card;

import com.damian.xBank.modules.banking.card.enums.BankingCardLockStatus;
import com.damian.xBank.modules.banking.card.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.exception.BankingCardAuthorizationException;
import com.damian.xBank.shared.domain.BankingCard;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;

public class BankingCardAuthorizationHelper {
    private Customer customer;
    private BankingCard card;

    public static BankingCardAuthorizationHelper authorize(Customer customer, BankingCard card) {
        BankingCardAuthorizationHelper helper = new BankingCardAuthorizationHelper();
        helper.card = card;
        helper.customer = customer;
        return helper;
    }

    /**
     * Check if the BankingCard belongs to this customer
     *
     * @return BankingCardAuthorizationHelper
     */
    public BankingCardAuthorizationHelper checkOwner() {
        if (!card.getOwner().getId().equals(customer.getId())) {
            // banking card does not belong to this customer
            throw new BankingCardAuthorizationException(
                    Exceptions.BANKING.CARD.ACCESS_FORBIDDEN,
                    card.getId(),
                    customer.getId()
            );
        }
        return this;
    }


    /**
     * Check if card is not disabled or locked
     *
     * @return BankingCardAuthorizationHelper
     */
    public BankingCardAuthorizationHelper checkStatus() {
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

    public BankingCardAuthorizationHelper checkPIN(String PIN) {
        // check card pin
        if (!card.getCardPin().equals(PIN)) {
            throw new BankingCardAuthorizationException(
                    Exceptions.BANKING.CARD.INVALID_PIN, card.getId(), 0L // TODO
            );
        }
        return this;
    }
}
