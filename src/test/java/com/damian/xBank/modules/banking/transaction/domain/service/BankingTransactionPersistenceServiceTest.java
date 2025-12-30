package com.damian.xBank.modules.banking.transaction.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingTransactionPersistenceServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionPersistenceService bankingTransactionPersistenceService;

    private Customer customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customerBankingCard = BankingCard
                .create(customerBankingAccount)
                .setId(1L);
    }

    @Test
    @DisplayName("Should record a transaction")
    void recordTransaction_ValidTransaction_SavesAndReturns() {
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
        assertThat(customerBankingAccount.getAccountTransactions())
                .contains(result);
        assertThat(result.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(result.getType()).isEqualTo(givenTransaction.getType());
        assertThat(result.getDescription()).isEqualTo(givenTransaction.getDescription());
        assertThat(customerBankingAccount.getAccountTransactions().size()).isEqualTo(1);
        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    }


}