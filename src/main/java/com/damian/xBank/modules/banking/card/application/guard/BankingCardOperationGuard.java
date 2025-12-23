package com.damian.xBank.modules.banking.card.application.guard;

import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;

import java.math.BigDecimal;

public class BankingCardOperationGuard {
    private final BankingCard card;

    public BankingCardOperationGuard(BankingCard card) {
        this.card = card;
    }

    public static BankingCardOperationGuard forCard(BankingCard card) {
        return new BankingCardOperationGuard(card);
    }

    /**
     * Validate a card can spend {@link #card}.
     *
     * @param amount  the amount to spend
     * @param cardPin the pin of the card
     * @return the current validator instance for chaining
     */
    public BankingCardOperationGuard assertCanSpend(
            BigDecimal amount,
            String cardPin
    ) {

        // check the account status and see if can be used to operate
        BankingCardGuard.forCard(card)
                        // check the funds from the sender account
                        .assertSufficientFunds(amount)
                        .assertCorrectPin(cardPin)
                        .assertUsable();

        return this;
    }

    /**
     * Assert that withdraw can be carried out in this {@link #card}.
     *
     * @param amount  the amount to spend
     * @param cardPin the pin of the card
     * @return the current validator instance for chaining
     */
    public BankingCardOperationGuard assertCanWithdraw(BigDecimal amount, String cardPin) {

        this.assertCanSpend(amount, cardPin);

        return this;
    }

}