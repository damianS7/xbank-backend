package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BankingCardGetAllTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private BankingCardGetAll bankingCardGetAll;

    private Customer customer;
    private BankingAccount bankingAccount;
    private BankingCard bankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        bankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        bankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("should return the cards of the current customer")
    void execute_WhenCalled_ReturnCustomerCards() {
        // given
        setUpContext(customer);

        // when
        when(bankingCardRepository.findCardsByCustomerId(anyLong())).thenReturn(
                Set.of(bankingCard)
        );

        Set<BankingCard> customerCards = bankingCardGetAll.execute();

        // then
        assertThat(customerCards)
                .isNotNull()
                .size()
                .isEqualTo(customerCards.size());

        verify(bankingCardRepository, times(1)).findCardsByCustomerId(anyLong());
    }
}