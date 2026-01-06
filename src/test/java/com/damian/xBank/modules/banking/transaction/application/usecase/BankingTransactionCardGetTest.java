package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class BankingTransactionCardGetTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionCardGet bankingTransactionCardGet;

    private User customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@demo.com")
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .build();

        customerBankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customerBankingCard = BankingCard
                .create(customerBankingAccount)
                .setId(1L);
    }

    @Test
    @DisplayName("should return all transactions from a card")
    void getCardTransactions_WhenValidCardId_ReturnsAllTransactions() {
        // given
        setUpContext(customer);

        Pageable pageable = PageRequest.of(0, 10);

        BankingTransaction givenTransaction = BankingTransaction
                .create(
                        BankingTransactionType.CARD_CHARGE,
                        customerBankingCard,
                        BigDecimal.valueOf(100)
                )
                .setId(1L)
                .setDescription("Deposit transaction");

        Page<BankingTransaction> page = new PageImpl<>(
                List.of(givenTransaction),
                pageable,
                1
        );

        // when
        when(bankingCardRepository.findById(customerBankingCard.getId()))
                .thenReturn(Optional.of(customerBankingCard));

        when(bankingTransactionRepository.findByBankingCardId(
                customerBankingCard.getId(), pageable))
                .thenReturn(page);

        Page<BankingTransaction> paginatedTransactions = bankingTransactionCardGet
                .execute(
                        customerBankingCard.getId(),
                        pageable
                );

        // then
        assertThat(paginatedTransactions)
                .isNotNull()
                .hasSize(1);
    }
}