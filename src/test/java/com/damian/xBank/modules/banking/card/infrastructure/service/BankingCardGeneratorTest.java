package com.damian.xBank.modules.banking.card.infrastructure.service;

import com.damian.xBank.shared.AbstractServiceTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BankingCardGeneratorTest extends AbstractServiceTest {

    private BankingCardGenerator bankingCardGenerator;

    @BeforeEach
    void setUp() {
        bankingCardGenerator = new BankingCardGeneratorImpl(new Faker());
    }

    @Test
    @DisplayName("should generate a card number")
    void generateCardNumber_WhenCalled_ReturnsCardNumber() {
        // given
        // when
        String generatedCardNumber = bankingCardGenerator.generateCardNumber();

        // then
        assertThat(generatedCardNumber)
                .isNotNull()
                .matches("\\d{16}");
    }

    @Test
    @DisplayName("should generate a card pin")
    void generateCardPin_WhenCalled_ReturnsPin() {
        // given
        // when
        String generatedCardPin = bankingCardGenerator.generatePin();

        // then
        assertThat(generatedCardPin)
                .isNotNull()
                .matches("\\d{4}");
    }

    @Test
    @DisplayName("Should generate a card cvv")
    void generateCvv_WhenCalled_ReturnsCvv() {
        // given
        // when
        String generatedCardCvv = bankingCardGenerator.generateCvv();

        // then
        assertThat(generatedCardCvv)
                .isNotNull()
                .matches("\\d{3}");
    }
}
