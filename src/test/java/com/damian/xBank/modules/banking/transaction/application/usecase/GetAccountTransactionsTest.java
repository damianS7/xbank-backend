package com.damian.xBank.modules.banking.transaction.application.usecase;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.account.GetAccountTransactions;
import com.damian.xBank.modules.banking.transaction.application.usecase.get.account.GetAccountTransactionsQuery;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.infrastructure.web.dto.response.PageResult;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.BankingAccountTestFactory;
import com.damian.xBank.test.utils.BankingTransactionTestFactory;
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

public class GetAccountTransactionsTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private GetAccountTransactions getAccountTransactions;

    private User customer;
    private BankingAccount customerBankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();

        customerBankingAccount = BankingAccountTestFactory.aSavingsAccount(customer)
            .withId(5L)
            .withBalance(BigDecimal.valueOf(1000))
            .build();
    }

    @Test
    @DisplayName("should return all account transactions")
    void getAccountTransactions_WhenValidAccountId_ReturnsAllTransactions() {
        // given
        setUpContext(customer);

        Pageable pageable = PageRequest.of(0, 10);

        BankingTransaction transaction = BankingTransactionTestFactory.aDepositTransaction()
            .withId(1L)
            .withAccount(customerBankingAccount)
            .withAmount(BigDecimal.valueOf(100))
            .withStatus(BankingTransactionStatus.PENDING)
            .withType(BankingTransactionType.DEPOSIT)
            .withDescription("Deposit transaction")
            .build();

        Page<BankingTransaction> page = new PageImpl<>(
            List.of(transaction),
            pageable,
            1
        );

        GetAccountTransactionsQuery query = new GetAccountTransactionsQuery(
            customerBankingAccount.getId(),
            pageable
        );

        // when
        when(bankingAccountRepository.findById(customerBankingAccount.getId()))
            .thenReturn(Optional.of(customerBankingAccount));

        when(bankingTransactionRepository.findByBankingAccount_Id(
            customerBankingAccount.getId(), pageable))
            .thenReturn(page);

        PageResult<BankingTransactionResult> result = getAccountTransactions.execute(query);

        // then
        assertThat(result.content())
            .isNotNull()
            .hasSize(1);
    }

}