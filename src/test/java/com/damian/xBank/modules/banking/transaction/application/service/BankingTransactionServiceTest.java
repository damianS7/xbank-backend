package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotFoundException;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionNotOwnerException;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingTransactionServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionService bankingTransactionService;

    private Customer customer;
    private BankingAccount customerBankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create()
                .setOwner(customer)
                .setId(5L)
                .setBalance(BigDecimal.valueOf(1000))
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");
    }

    @Test
    @DisplayName("Should get a transaction by id")
    void shouldGetTransactionById() {
        // given
        setUpContext(customer);

        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.findById(givenTransaction.getId()))
                .thenReturn(Optional.of(givenTransaction));

        BankingTransaction retrievedTransaction = bankingTransactionService
                .getTransaction(givenTransaction.getId());

        // then
        assertThat(retrievedTransaction).isNotNull();
        assertThat(retrievedTransaction.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(retrievedTransaction.getType()).isEqualTo(givenTransaction.getType());
        assertThat(retrievedTransaction.getDescription()).isEqualTo(givenTransaction.getDescription());
    }

    @Test
    @DisplayName("Should fail to get a transaction by id when not exists")
    void shouldGetTransactionByIdWhenNotExists() {
        // given
        setUpContext(customer);

        // when
        when(bankingTransactionRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        BankingTransactionNotFoundException exception = assertThrows(
                BankingTransactionNotFoundException.class,
                () -> bankingTransactionService.getTransaction(1L)

        );
        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_FOUND);
    }

    @Test
    @DisplayName("Should fail to get a transaction by id when not owner")
    void shouldGetTransactionByIdWhenNotOwner() {
        // given
        setUpContext(customer);

        Customer otherCustomer = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("otherCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        customerBankingAccount.setOwner(otherCustomer);

        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(givenTransaction));

        BankingTransactionNotOwnerException exception = assertThrows(
                BankingTransactionNotOwnerException.class,
                () -> bankingTransactionService.getTransaction(givenTransaction.getId())

        );
        // then
        assertThat(exception)
                .isNotNull()
                .hasMessage(ErrorCodes.BANKING_TRANSACTION_NOT_OWNER);
    }

    @Test
    @DisplayName("Should get account transactions by id")
    void shouldGetAccountTransactionsById() {
        // given
        setUpContext(customer);

        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.findById(givenTransaction.getId()))
                .thenReturn(Optional.of(givenTransaction));

        BankingTransaction retrievedTransaction = bankingTransactionService
                .getTransaction(givenTransaction.getId());

        // then
        assertThat(retrievedTransaction).isNotNull();
        assertThat(retrievedTransaction.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(retrievedTransaction.getType()).isEqualTo(givenTransaction.getType());
        assertThat(retrievedTransaction.getDescription()).isEqualTo(givenTransaction.getDescription());
    }

    @Test
    @DisplayName("Should get account transactions by id")
    void shouldGetCardTransactionsById() {
        // given
        setUpContext(customer);

        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.findById(givenTransaction.getId()))
                .thenReturn(Optional.of(givenTransaction));

        BankingTransaction retrievedTransaction = bankingTransactionService
                .getTransaction(givenTransaction.getId());

        // then
        assertThat(retrievedTransaction).isNotNull();
        assertThat(retrievedTransaction.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(retrievedTransaction.getType()).isEqualTo(givenTransaction.getType());
        assertThat(retrievedTransaction.getDescription()).isEqualTo(givenTransaction.getDescription());
    }

    @Test
    @DisplayName("Should record a transaction")
    void shouldRecordTransaction() {
        // given
        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankingTransaction result = bankingTransactionService
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

    @Test
    @DisplayName("Should confirm a transaction")
    void shouldCompleteTransaction() {
        // given
        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setStatus(BankingTransactionStatus.PENDING)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankingTransaction result = bankingTransactionService
                .complete(givenTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BankingTransactionStatus.COMPLETED);
        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    }

    @Test
    @DisplayName("Should reject a transaction")
    void shouldRejectTransaction() {
        // given
        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setStatus(BankingTransactionStatus.PENDING)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        // when
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankingTransaction result = bankingTransactionService
                .reject(givenTransaction);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BankingTransactionStatus.REJECTED);
        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    }
}