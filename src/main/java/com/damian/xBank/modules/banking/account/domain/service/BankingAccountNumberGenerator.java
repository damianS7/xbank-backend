package com.damian.xBank.modules.banking.account.domain.service;

public interface BankingAccountNumberGenerator {
    String generate();
    
    String getBIN();
}