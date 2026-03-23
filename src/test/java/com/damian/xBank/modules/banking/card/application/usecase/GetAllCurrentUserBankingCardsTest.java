package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserBankingCards;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserBankingCardsResult;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserCardsQuery;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardTestBuilder;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllCurrentUserBankingCardsTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private GetAllCurrentUserBankingCards getAllCurrentUserBankingCards;

    private User customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.builder()
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
    @DisplayName("should return the cards of the current customer")
    void getAllCards_WhenCalled_ReturnsCustomerCards() {
        // given
        setUpContext(customer);
        GetAllCurrentUserCardsQuery query = new GetAllCurrentUserCardsQuery();

        // when
        when(bankingCardRepository.findCardsByUserId(anyLong())).thenReturn(
            Set.of(bankingCard)
        );

        GetAllCurrentUserBankingCardsResult result = getAllCurrentUserBankingCards.execute(query);

        // then
        assertThat(result.cards())
            .isNotNull()
            .size()
            .isGreaterThanOrEqualTo(1);

        verify(bankingCardRepository, times(1)).findCardsByUserId(anyLong());
    }
}