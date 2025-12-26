package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingTransactionServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionService bankingTransactionService;

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
                .create()
                .setOwner(customer)
                .setId(5L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customerBankingCard = BankingCard
                .create()
                .setBankingAccount(customerBankingAccount)
                .setId(1L);
    }

    @Test
    @DisplayName("Should get a transaction when exists and is owner")
    void getTransaction_ExistsAndIsOwner_ReturnsTransaction() {
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
    @DisplayName("Should fail to get a transaction when not exists")
    void getTransaction_NotExists_ThrowNotFoundException() {
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
    @DisplayName("Should fail to get a transaction when exist but customer its not owner")
    void getTransaction_NotOwner_ThrowNotOwnerException() {
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
    @DisplayName("Should get all account transactions")
    void getAccountTransactions_ValidAccountId_ReturnsAllTransactions() {
        // given
        setUpContext(customer);

        Pageable pageable = PageRequest.of(0, 10);

        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingAccount(customerBankingAccount)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.DEPOSIT)
                .setDescription("Deposit transaction");

        Page<BankingTransaction> page = new PageImpl<>(
                List.of(givenTransaction),
                pageable,
                1
        );

        // when
        when(bankingAccountRepository.findById(customerBankingAccount.getId()))
                .thenReturn(Optional.of(customerBankingAccount));

        when(bankingTransactionRepository.findByBankingAccountId(
                customerBankingAccount.getId(), pageable))
                .thenReturn(page);

        Page<BankingTransaction> paginatedTransactions = bankingTransactionService
                .getAccountTransactions(
                        customerBankingAccount.getId(),
                        pageable
                );

        // then
        assertThat(paginatedTransactions)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("Should get card transactions")
    void getCardTransactions_ValidCardId_ReturnsAllTransactions() {
        // given
        setUpContext(customer);

        Pageable pageable = PageRequest.of(0, 10);

        BankingTransaction givenTransaction = BankingTransaction
                .create()
                .setId(1L)
                .setBankingCard(customerBankingCard)
                .setAmount(BigDecimal.valueOf(100))
                .setType(BankingTransactionType.CARD_CHARGE)
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

        Page<BankingTransaction> paginatedTransactions = bankingTransactionService
                .getCardTransactions(
                        customerBankingCard.getId(),
                        pageable
                );

        // then
        assertThat(paginatedTransactions)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("Should record a transaction")
    void recordTransaction_ValidTransaction_SavesAndReturns() {
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
    void completeTransaction_PendingTransaction_ChangesStatusToCompleted() {
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
    void rejectTransaction_PendingTransaction_ChangesStatusToRejected() {
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