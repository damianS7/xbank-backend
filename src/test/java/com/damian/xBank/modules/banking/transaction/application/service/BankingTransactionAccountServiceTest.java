package com.damian.xBank.modules.banking.transaction.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.application.dto.request.BankingTransactionUpdateStatusRequest;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.exception.BankingTransactionStatusTransitionException;
import com.damian.xBank.modules.banking.transaction.infra.repository.BankingTransactionRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BankingTransactionAccountServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingTransactionRepository bankingTransactionRepository;

    @InjectMocks
    private BankingTransactionAccountService bankingTransactionAccountService;

    private Customer customer;
    private BankingAccount customerBankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
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
    @DisplayName("Should build a transaction")
    void shouldBuildTransaction() {
        // given
        String givenDescription = "Account deposit";
        BigDecimal givenAmount = BigDecimal.valueOf(100);
        BankingTransactionType givenTransactionType = BankingTransactionType.DEPOSIT;

        // when
        BankingTransaction createdBankingTransaction = bankingTransactionAccountService
                .buildTransaction(
                        customerBankingAccount,
                        givenTransactionType,
                        givenAmount,
                        givenDescription
                );

        // then
        assertThat(createdBankingTransaction).isNotNull();
        assertThat(createdBankingTransaction.getAmount()).isEqualTo(givenAmount);
        assertThat(createdBankingTransaction.getType()).isEqualTo(givenTransactionType);
        assertThat(createdBankingTransaction.getDescription()).isEqualTo(givenDescription);
    }

    @Test
    @DisplayName("Should record a transaction")
    void shouldRecordTransaction() {
        // given
        BankingTransaction givenTransaction = new BankingTransaction(customerBankingAccount);
        givenTransaction.setAmount(BigDecimal.valueOf(100));
        givenTransaction.setType(BankingTransactionType.DEPOSIT);
        givenTransaction.setDescription("Account deposit");

        // when
        when(bankingTransactionRepository.save(any(BankingTransaction.class)))
                .thenReturn(givenTransaction);

        BankingTransaction createdBankingTransaction = bankingTransactionAccountService
                .recordTransaction(givenTransaction);

        // then
        assertThat(createdBankingTransaction).isNotNull();
        assertThat(createdBankingTransaction.getAmount()).isEqualTo(givenTransaction.getAmount());
        assertThat(createdBankingTransaction.getType()).isEqualTo(givenTransaction.getType());
        assertThat(createdBankingTransaction.getDescription()).isEqualTo(givenTransaction.getDescription());
        assertThat(customerBankingAccount.getAccountTransactions().size()).isEqualTo(1);
        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    }


    @Test
    @DisplayName("Should update transaction status.")
    void shouldUpdateTransactionStatus() {
        // given
        setUpContext(customer);

        BankingTransactionUpdateStatusRequest request = new BankingTransactionUpdateStatusRequest(
                BankingTransactionStatus.COMPLETED
        );

        BankingTransaction bankingTransaction = new BankingTransaction();
        bankingTransaction.setId(1L);
        bankingTransaction.setBankingAccount(customerBankingAccount);
        bankingTransaction.setAmount(BigDecimal.valueOf(100));
        bankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        bankingTransaction.setDescription("Amazon.com");
        bankingTransaction.setStatus(BankingTransactionStatus.PENDING);

        // when
        when(bankingTransactionRepository.findById(bankingTransaction.getId())).thenReturn(Optional.of(
                bankingTransaction));

        when(bankingTransactionRepository.save(any(BankingTransaction.class))).thenReturn(bankingTransaction);

        BankingTransaction savedTransaction =
                bankingTransactionAccountService.updateTransactionStatus(bankingTransaction.getId(), request);

        // then
        assertThat(savedTransaction.getStatus()).isEqualTo(request.transactionStatus());
        verify(bankingTransactionRepository, times(1)).save(any(BankingTransaction.class));
    }

    @Test
    @DisplayName("Should fail to update transaction status to invalid status")
    void shouldFailToUpdateTransactionStatusToInvalidStatus() {
        // given
        setUpContext(customer);

        BankingTransactionUpdateStatusRequest request = new BankingTransactionUpdateStatusRequest(
                BankingTransactionStatus.COMPLETED
        );

        BankingTransaction bankingTransaction = new BankingTransaction();
        bankingTransaction.setId(1L);
        bankingTransaction.setBankingAccount(customerBankingAccount);
        bankingTransaction.setAmount(BigDecimal.valueOf(100));
        bankingTransaction.setType(BankingTransactionType.CARD_CHARGE);
        bankingTransaction.setDescription("Amazon.com");
        bankingTransaction.setStatus(BankingTransactionStatus.REJECTED);

        // when
        when(bankingTransactionRepository.findById(bankingTransaction.getId())).thenReturn(Optional.of(
                bankingTransaction));

        BankingTransactionStatusTransitionException exception =
                assertThrows(
                        BankingTransactionStatusTransitionException.class,
                        () -> bankingTransactionAccountService.updateTransactionStatus(
                                bankingTransaction.getId(),
                                request
                        )
                );

        // then
        //        assertThat(exception.getMessage()).isEqualTo(request.transactionStatus());
        verify(bankingTransactionRepository, times(0)).save(any(BankingTransaction.class));
    }
}