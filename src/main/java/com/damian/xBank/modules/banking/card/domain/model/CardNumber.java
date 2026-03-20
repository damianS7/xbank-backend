package com.damian.xBank.modules.banking.card.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public class CardNumber {

    @NotBlank
    @Size(min = 16, max = 16)
    @Column(name = "cardNumber", nullable = false)
    private String cardNumber;

    // Constructor JPA
    protected CardNumber() {
    }

    public CardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }

        this.cardNumber = cardNumber.replace(" ", "");

        if (this.cardNumber.length() != 16) {
            throw new IllegalArgumentException("Card number must have 16 characters");
        }
    }

    public static CardNumber of(String cardNumber) {
        return new CardNumber(cardNumber);
    }

    public static CardNumber from(BankingCard card) {
        return new CardNumber(card.getCardNumber());
    }

    public String getValue() {
        return cardNumber;
    }

    @Override
    public String toString() {
        return cardNumber;
    }
}