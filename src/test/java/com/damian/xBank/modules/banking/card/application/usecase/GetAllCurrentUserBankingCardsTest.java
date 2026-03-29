package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserBankingCards;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserBankingCardsResult;
import com.damian.xBank.modules.banking.card.application.usecase.get.GetAllCurrentUserCardsQuery;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
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