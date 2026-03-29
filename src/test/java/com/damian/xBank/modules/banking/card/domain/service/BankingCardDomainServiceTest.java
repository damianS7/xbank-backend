package com.damian.xBank.modules.banking.card.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class BankingCardDomainServiceTest extends AbstractServiceTest {

    @Mock
    private BankingCardGenerator bankingCardGenerator;

    @InjectMocks
    private BankingCardDomainService bankingCardDomainService;

    private User customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        bankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();

        bankingCard = BankingCardTestFactory.aDebitCard(bankingAccount)
            .withId(11L)
            .build();
    }

    @Test
    @DisplayName("should return the customer banking card created")
    void createBankingCard_WhenValidRequest_ReturnsBankingCard() {
        // given
        // when
        when(bankingCardGenerator.generateCardNumber())
            .thenReturn("1234123412341234");
        when(bankingCardGenerator.generateCvv())
            .thenReturn("123");
        when(bankingCardGenerator.generatePin())
            .thenReturn("1234");

        BankingCard createdCard = bankingCardDomainService.createBankingCard(
            bankingAccount,
            BankingCardType.DEBIT
        );

        // then
        assertThat(createdCard)
            .isNotNull()
            .extracting(
                BankingCard::getBankingAccount,
                BankingCard::getCardType
            )
            .containsExactly(
                bankingAccount,
                createdCard.getCardType()
            );
    }
}
