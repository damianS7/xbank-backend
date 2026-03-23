package com.damian.xBank.modules.banking.card.domain.service;

public interface BankingCardGenerator {
    String generateCardNumber();

    String generateCvv();

    String generatePin();
}