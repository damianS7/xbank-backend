package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.card.GetCardTransactions;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.card.GetCardTransactionsQuery;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingCardTestFactory;
import com.damian.xBank.test.utils.UserTestFactory;
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

public class GetCardTransactionsTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private GetCardTransactions getCardTransactions;

    private User customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        customerBankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();

        customerBankingCard = BankingCardTestFactory.aDebitCard(customerBankingAccount)
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should return all transactions from a card")
    void getCardTransactions_WhenValidCardId_ReturnsAllTransactions() {
        // given
        setUpContext(customer);

        Pageable pageable = PageRequest.of(0, 10);

        BankingTransaction transaction = BankingTransaction.createCardTransaction(
            BankingTransactionType.CARD_CHARGE,
            customerBankingCard,
            BigDecimal.valueOf(100),
            "Amazon.com"
        );

        Page<BankingTransaction> page = new PageImpl<>(
            List.of(transaction),
            pageable,
            1
        );

        GetCardTransactionsQuery query = new GetCardTransactionsQuery(
            customerBankingCard.getId(),
            pageable
        );

        // when
        when(bankingCardRepository.findById(customerBankingCard.getId()))
            .thenReturn(Optional.of(customerBankingCard));

        when(bankingTransactionRepository.findByBankingCard_Id(
            customerBankingCard.getId(), pageable))
            .thenReturn(page);

        PageResult<BankingTransactionResult> result = getCardTransactions.execute(query);

        // then
        assertThat(result.content())
            .isNotNull()
            .hasSize(1);
    }
}