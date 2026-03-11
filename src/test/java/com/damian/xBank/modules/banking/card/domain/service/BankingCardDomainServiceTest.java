package com.damian.xBank.modules.banking.card.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.BankingAccountTestBuilder;
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

        customer.addBankingAccount(bankingAccount);

        bankingCard = BankingCard
            .create(bankingAccount)
            .setId(11L)
            .setCardNumber("1234123412341234")
            .setCardCvv("123")
            .setCardPin("1234");
    }

    @Test
    @DisplayName("should return the customer banking card created")
    void createBankingCard_WhenValidRequest_ReturnsBankingCard() {
        // given
        // when
        when(bankingCardGenerator.generate(bankingAccount, bankingCard.getCardType()))
            .thenReturn(bankingCard);

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
                bankingCard.getCardType()
            );
    }
}
