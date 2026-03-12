package com.damian.xBank.modules.banking.transaction.infrastructure.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountTestBuilder;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BankingTransactionPersistenceServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    private User customer;
    private BankingAccount customerBankingAccount;

    @BeforeEach
    void setUp() {
        customer = UserTestBuilder.aCustomer()
            .withId(1L)
            .withEmail("customer@demo.com")
            .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
            .build();

        customerBankingAccount = BankingAccountTestBuilder.builder()
            .withId(5L)
            .withOwner(customer)
            .withCurrency(BankingAccountCurrency.EUR)
            .withBalance(BigDecimal.valueOf(1000))
            .withType(BankingAccountType.SAVINGS)
            .withAccountNumber("US1200001111112233335555")
            .build();
    }

    @Test
    @DisplayName("should return a saved transaction when valid transaction is provided")
    void recordTransaction_WhenValidTransaction_ReturnsSavedTransaction() {
        // given
        BankingTransaction givenTransaction = BankingTransaction
            .create(
                BankingTransactionType.DEPOSIT,
                customerBankingAccount,
                BigDecimal.valueOf(100)
            )
            .setId(1L)
            .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        BankingTransaction result = bankingTransactionPersistenceService
            .record(givenTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(result.getType()).isEqualTo(givenTransaction.getType());
        assertThat(result.getDescription()).isEqualTo(givenTransaction.getDescription());
        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    }
}