package com.damian.xBank.modules.banking.card.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardTestBuilder;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        bankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();

        bankingCard = BankingCardTestBuilder.builder()
            .withId(11L)
            .withOwnerAccount(bankingAccount)
            .withCardNumber("1234123412341234")
            .withStatus(BankingCardStatus.ACTIVE)
            .withCVV("123")
            .withPIN("1234")
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
