package com.damian.xBank.modules.banking.account.infrastructure.service;

import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BankingAccountNumberGeneratorTest extends AbstractServiceTest {
    private BankingAccountNumberGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new BankingAccountNumberGeneratorImpl("ES880099");
    }

    @Test
    @DisplayName("should return a valid banking account number")
    void generate_ReturnsValidAccountNumber() {
        // when
        String accountNumber = generator.generate();

        // then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber).hasSize(24);

        // country code (2 uppercase letters)
        assertThat(accountNumber.substring(0, 2))
                .matches("[A-Z]{2}");

        // remaining digits
        assertThat(accountNumber.substring(2))
                .matches("\\d{22}");
    }
}
